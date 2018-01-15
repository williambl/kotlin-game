package main.kotlin.com.williambl

import com.williambl.util.createShader
import com.williambl.util.createWindow
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.MemoryUtil
import java.io.IOException
import java.nio.IntBuffer

class Engine {

    private var errorCallback : GLFWErrorCallback? = null
    private var keyCallback : GLFWKeyCallback? = null

    private var window : Long? = null
    private var width : Int = 800
    private var height : Int = 800

    private var cubeVAO: Int? = null
    private var cubeProgram: Int? = null

    private var viewMatrixUniform: Int = 0
    private var projMatrixUniform: Int = 0
    private var viewportSizeUniform: Int = 0
    internal var viewMatrix = Matrix4f()
    internal var projMatrix = Matrix4f()
    internal var matrixBuffer = BufferUtils.createFloatBuffer(16)

    private fun init() {

        //Create an error callback
        errorCallback = glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))

        //Try to init glfw
        if (!glfwInit())
            throw IllegalStateException("Unable to initialize GLFW")

        //Set up our window
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

        // Create the window
        window = createWindow(width, height, "Cube!")
        if (window == MemoryUtil.NULL) {
            throw AssertionError("Failed to create the GLFW window")
        }

        // Set up a framebuffer size callback. It will be called every time the framebuffer is resized
        glfwSetFramebufferSizeCallback(window!!, object : GLFWFramebufferSizeCallback() {
            override fun invoke(window: Long, width: Int, height: Int) {
                if (width > 0 && height > 0
                        && (width != width || height != height)) {
                    this@Engine.width = width
                    this@Engine.height = height
                }
            }
        })

        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        keyCallback = glfwSetKeyCallback(window!!, object : GLFWKeyCallback() {
            override fun invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
                    glfwSetWindowShouldClose(window, true)
                }

            }
        })

        // Get the resolution of the primary monitor
        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

        // Center our window
        glfwSetWindowPos(
                window!!,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window!!)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window!!)

        GL.createCapabilities()
        GLUtil.setupDebugMessageCallback()

        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        /* Create all needed GL resources */
        createCubeVao()
        createCubeProgram()
        initCubeProgram()
    }

    private fun quadPattern(vb: IntBuffer) {
        vb.put(1).put(0).put(1).put(1).put(0).put(1)
    }

    private fun quadWithDiagonalPattern(vb: IntBuffer) {
        vb.put(1).put(1).put(1).put(1).put(1).put(1)
    }

    private fun createCubeVao() {
        this.cubeVAO = glGenVertexArrays()
        glBindVertexArray(cubeVAO!!)
        val vb = BufferUtils.createIntBuffer(6 * 6)
        val pb = BufferUtils.createFloatBuffer(3 * 6 * 6)
        quadPattern(vb)
        pb.put(0.5f).put(0.5f).put(-0.5f)
        pb.put(0.5f).put(-0.5f).put(-0.5f)
        pb.put(-0.5f).put(-0.5f).put(-0.5f)
        pb.put(-0.5f).put(-0.5f).put(-0.5f)
        pb.put(-0.5f).put(0.5f).put(-0.5f)
        pb.put(0.5f).put(0.5f).put(-0.5f)
        quadWithDiagonalPattern(vb)
        pb.put(0.5f).put(-0.5f).put(0.5f)
        pb.put(0.5f).put(0.5f).put(0.5f)
        pb.put(-0.5f).put(0.5f).put(0.5f)
        pb.put(-0.5f).put(0.5f).put(0.5f)
        pb.put(-0.5f).put(-0.5f).put(0.5f)
        pb.put(0.5f).put(-0.5f).put(0.5f)
        quadPattern(vb)
        pb.put(0.5f).put(-0.5f).put(-0.5f)
        pb.put(0.5f).put(0.5f).put(-0.5f)
        pb.put(0.5f).put(0.5f).put(0.5f)
        pb.put(0.5f).put(0.5f).put(0.5f)
        pb.put(0.5f).put(-0.5f).put(0.5f)
        pb.put(0.5f).put(-0.5f).put(-0.5f)
        quadWithDiagonalPattern(vb)
        pb.put(-0.5f).put(-0.5f).put(0.5f)
        pb.put(-0.5f).put(0.5f).put(0.5f)
        pb.put(-0.5f).put(0.5f).put(-0.5f)
        pb.put(-0.5f).put(0.5f).put(-0.5f)
        pb.put(-0.5f).put(-0.5f).put(-0.5f)
        pb.put(-0.5f).put(-0.5f).put(0.5f)
        quadPattern(vb)
        pb.put(0.5f).put(0.5f).put(0.5f)
        pb.put(0.5f).put(0.5f).put(-0.5f)
        pb.put(-0.5f).put(0.5f).put(-0.5f)
        pb.put(-0.5f).put(0.5f).put(-0.5f)
        pb.put(-0.5f).put(0.5f).put(0.5f)
        pb.put(0.5f).put(0.5f).put(0.5f)
        quadWithDiagonalPattern(vb)
        pb.put(0.5f).put(-0.5f).put(-0.5f)
        pb.put(0.5f).put(-0.5f).put(0.5f)
        pb.put(-0.5f).put(-0.5f).put(0.5f)
        pb.put(-0.5f).put(-0.5f).put(0.5f)
        pb.put(-0.5f).put(-0.5f).put(-0.5f)
        pb.put(0.5f).put(-0.5f).put(-0.5f)
        pb.flip()
        vb.flip()
        // setup vertex positions buffer
        val posVbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, posVbo)
        glBufferData(GL_ARRAY_BUFFER, pb, GL_STATIC_DRAW)
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L)
        // setup vertex visibility buffer
        val visVbo = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, visVbo)
        glBufferData(GL_ARRAY_BUFFER, vb, GL_STATIC_DRAW)
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(1, 1, GL_UNSIGNED_INT, false, 0, 0L)
        glBindVertexArray(0)
    }

    @Throws(IOException::class)
    private fun createCubeProgram() {
        val program = glCreateProgram()
        val vshader = createShader("com/williambl/kotlin-game/vs.glsl", GL_VERTEX_SHADER)
        val fshader = createShader("com/williambl/kotlin-game/fs.glsl", GL_FRAGMENT_SHADER)
        val gshader = createShader("com/williambl/kotlin-game/gs.glsl", GL_GEOMETRY_SHADER)
        glAttachShader(program, vshader)
        glAttachShader(program, fshader)
        glAttachShader(program, gshader)
        glBindAttribLocation(program, 0, "position")
        glBindAttribLocation(program, 1, "visible")
        glBindFragDataLocation(program, 0, "color")
        glLinkProgram(program)
        val linked = glGetProgrami(program, GL_LINK_STATUS)
        val programLog = glGetProgramInfoLog(program)
        if (programLog != null && programLog.trim({ it <= ' ' }).length > 0) {
            System.err.println(programLog)
        }
        if (linked == 0) {
            throw AssertionError("Could not link cubeProgram")
        }
        this.cubeProgram = program
    }

    /**
     * Initialize the shader cubeProgram.
     */
    private fun initCubeProgram() {
        glUseProgram(this.cubeProgram!!)
        viewMatrixUniform = glGetUniformLocation(this.cubeProgram!!, "viewMatrix")
        projMatrixUniform = glGetUniformLocation(this.cubeProgram!!, "projMatrix")
        viewportSizeUniform = glGetUniformLocation(this.cubeProgram!!, "viewportSize")
        glUseProgram(0)
    }

    private fun loop() {
        while (!glfwWindowShouldClose(window!!)) {
            glfwPollEvents()
            glViewport(0, 0, width, height)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            update()
            render()

            glfwSwapBuffers(window!!)
        }
    }

    internal var angle = 0.0f
    internal var lastTime = System.nanoTime()

    //Any game code would go in here.
    internal fun update() {
        projMatrix.setPerspective(Math.toRadians(30.0).toFloat(), width.toFloat() / height, 0.01f, 50.0f)
        val thisTime = System.nanoTime()
        val diff = (thisTime - lastTime) / 1E9f
        angle += diff
        viewMatrix.setLookAt(0.0f, 2.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f).rotateY(angle)
        lastTime = thisTime
    }

    private fun render() {
        glUseProgram(this.cubeProgram!!)

        glUniformMatrix4fv(viewMatrixUniform, false, viewMatrix.get(matrixBuffer))
        glUniformMatrix4fv(projMatrixUniform, false, projMatrix.get(matrixBuffer))
        glUniform2f(viewportSizeUniform, width.toFloat(), height.toFloat())

        glBindVertexArray(cubeVAO!!)
        glDrawArrays(GL_TRIANGLES, 0, 3 * 2 * 6)
        glBindVertexArray(0)

        glUseProgram(0)
    }

    fun run() {

        try {

            init()
            loop()
            // Destroy window
            glfwDestroyWindow(window!!);
            keyCallback?.close()

        } finally {

            // Terminate GLFW
            glfwTerminate()
            errorCallback?.close()

        }
    }
}
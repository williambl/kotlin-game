package main.kotlin.com.williambl

import com.oracle.util.Checksums.update
import com.sun.scenario.effect.impl.prism.PrEffectHelper.render
import com.williambl.util.createShader
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.MemoryUtil
import java.io.IOException
import java.lang.RuntimeException
import java.nio.IntBuffer

class Engine {

    private var errorCallback : GLFWErrorCallback? = null
    private var keyCallback : GLFWKeyCallback? = null

    private var window : Long? = null
    private var width : Int = 800
    private var height : Int = 800

    private var vao : Int? = null
    private var program: Int? = null

    private var viewMatrixUniform: Int = 0
    private var projMatrixUniform: Int = 0
    private var viewportSizeUniform: Int = 0
    internal var viewMatrix = javax.vecmath.Matrix4f()
    internal var projMatrix = javax.vecmath.Matrix4f()
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
        window = glfwCreateWindow(width, height, "Currently nothing, just a blank screen...", MemoryUtil.NULL, MemoryUtil.NULL)
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

        //Code from lwjgldemos, not working yet
        //caps = GL.createCapabilities()
        //debugProc = GLUtil.setupDebugMessageCallback()

        //glEnable(GL_DEPTH_TEST)
        //glEnable(GL_CULL_FACE)

        /* Create all needed GL resources */
        createVao()
        createRasterProgram()
        initProgram()
    }

    internal fun quadPattern(vb: IntBuffer) {
        vb.put(1).put(0).put(1).put(1).put(0).put(1)
    }

    internal fun quadWithDiagonalPattern(vb: IntBuffer) {
        vb.put(1).put(1).put(1).put(1).put(1).put(1)
    }

    internal fun createVao() {
        this.vao = glGenVertexArrays()
        glBindVertexArray(vao)
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
    internal fun createRasterProgram() {
        val program = glCreateProgram()
        val vshader = createShader("org/lwjgl/demo/opengl/geometry/vs.glsl", GL_VERTEX_SHADER)
        val fshader = createShader("org/lwjgl/demo/opengl/geometry/fs.glsl", GL_FRAGMENT_SHADER)
        val gshader = createShader("org/lwjgl/demo/opengl/geometry/gs.glsl", GL_GEOMETRY_SHADER)
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
            throw AssertionError("Could not link program")
        }
        this.program = program
    }

    /**
     * Initialize the shader program.
     */
    internal fun initProgram() {
        glUseProgram(this.program!!)
        viewMatrixUniform = glGetUniformLocation(this.program!!, "viewMatrix")
        projMatrixUniform = glGetUniformLocation(this.program!!, "projMatrix")
        viewportSizeUniform = glGetUniformLocation(this.program!!, "viewportSize")
        glUseProgram(0)
    }

    private fun loop() {

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Set the clear color
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window!!)) {

            // Clear the framebuffer
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)

            // Swap the color buffers
            glfwSwapBuffers(window!!);

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

    }

    internal fun loop() {
        while (!glfwWindowShouldClose(window!!)) {
            glfwPollEvents()
            glViewport(0, 0, width, height)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            update()
            render()

            glfwSwapBuffers(window!!)
        }
    }

    //Any game code would go in here.
    internal fun update() {
        projMatrix.setPerspective(Math.toRadians(30.0).toFloat(), width.toFloat() / height, 0.01f, 50.0f)
        val thisTime = System.nanoTime()
        val diff = (thisTime - lastTime) / 1E9f
        angle += diff
        viewMatrix.setLookAt(0.0f, 2.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f).rotateY(angle)
        lastTime = thisTime
    }

    internal fun render() {
        glUseProgram(this.program!!)

        glUniformMatrix4fv(viewMatrixUniform, false, viewMatrix.get(matrixBuffer))
        glUniformMatrix4fv(projMatrixUniform, false, projMatrix.get(matrixBuffer))
        glUniform2f(viewportSizeUniform, width.toFloat(), height.toFloat())

        glBindVertexArray(vao!!)
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
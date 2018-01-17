package main.kotlin.com.williambl

import com.williambl.Game
import com.williambl.renderObject.RenderObjectCube
import com.williambl.renderObject.RenderObject
import com.williambl.util.createWindow
import org.joml.Matrix4f
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.MemoryUtil

class Engine {

    internal var game = Game(this)

    private var errorCallback : GLFWErrorCallback? = null
    private var keyCallback : GLFWKeyCallback? = null

    private var window : Long? = null
    internal var width : Int = 800
    internal var height : Int = 800

    private var renderObjects = mutableListOf<RenderObject>()

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
        window = createWindow(width, height, "GameObjectCube!")
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
        game.init()
        updateRenderObjs()
    }

    internal fun updateRenderObjs () {
        game.gameObjs
                .filter { it.renderObj != null }
                .forEach { renderObjects.add(it.renderObj!!) }
    }

    private fun loop() {
        while (!glfwWindowShouldClose(window!!)) {
            glfwPollEvents()
            glViewport(0, 0, width, height)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            game.update()
            render()

            glfwSwapBuffers(window!!)
        }
    }

    private fun render() {
        for(rendObj in renderObjects) {
            rendObj.render()
        }
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
package main.kotlin.com.williambl

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWFramebufferSizeCallback
import org.lwjgl.glfw.GLFWKeyCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GLUtil
import org.lwjgl.system.MemoryUtil
import java.lang.RuntimeException

class Engine {

    private var errorCallback : GLFWErrorCallback? = null
    private var keyCallback : GLFWKeyCallback? = null

    private var window : Long? = null
    private var width : Int = 800
    private var height: Int = 800

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
        //createVao()
        //createRasterProgram()
        //initProgram()
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
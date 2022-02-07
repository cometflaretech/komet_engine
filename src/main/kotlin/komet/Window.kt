package komet

import komet.scene.Scene
import komet.editor.ImGuiLayer
import komet.gfx.FrameBuffer
import komet.gfx.SpriteSheet
import komet.gfx.debug.DebugDraw
import komet.scene.SceneSerialization
import komet.util.AssetPool
import komet.util.Time
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

object Window {
    fun getTargetAspectRatio() = 16f / 9f

    var width: Int = 2560
        private set

    var height: Int = 1440
        private set

    var title: String = "Komet Engine v.0.1.0-alpha.dev.30"
        private set

    var internalWindow: Long = NULL
        private set

    var currentScene: Scene? = null
        private set

    var frameBuffer: FrameBuffer? = null
        private set

    var r = 1f
    var g = 1f
    var b = 1f
    var a = 1f

    private var imguiLayer: ImGuiLayer? = null

    fun run() {
        init()
        loop()
        freeMemory()
    }

    private fun init() {
        // Setup GLFW error callback
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW library
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW library.")
        }

        // Configure GLFW
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        //glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE) todo. fix imgui

        // Create the GLFW window
        internalWindow = glfwCreateWindow(width, height, title, NULL, NULL)
        if (internalWindow == NULL) {
            throw IllegalStateException("Failed to create the GLFW window.")
        }

        // Register mouse event callbacks
        glfwSetCursorPosCallback(internalWindow, MouseListener::mousePosCallback)
        glfwSetMouseButtonCallback(internalWindow, MouseListener::mouseButtonCallback)
        glfwSetScrollCallback(internalWindow, MouseListener::mouseScrollCallback)

        // Register keyboard event callbacks
        glfwSetKeyCallback(internalWindow, KeyListener::keyCallback)

        // Register window event callbacks
        glfwSetWindowSizeCallback(internalWindow) { _: Long, newWidth: Int, newHeight: Int ->
            width = newWidth
            height = newHeight
        }

        // Create the OpenGL current context
        glfwMakeContextCurrent(internalWindow)
        // Enable v-sync
        glfwSwapInterval(1)

        // Make the GLFW window visible to the screen
        glfwShowWindow(internalWindow)

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Enable alpha blending
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        // Initialize ImGui library
        imguiLayer = ImGuiLayer(internalWindow).also { it.initImGui() }

        frameBuffer = FrameBuffer(3840, 2160)
        glViewport(0, 0, 3840, 2160)

        // Load resources
        AssetPool.addSpriteSheet(
            "assets/textures/stars.png",
            SpriteSheet(128, 128, 8, 0),
        )
        AssetPool.addSpriteSheet(
            "assets/textures/world.png",
            SpriteSheet(512, 512, 8, 0),
        )
        AssetPool.addSpriteSheet(
            "assets/textures/pk3_island_tilemap.png",
            SpriteSheet(128, 128, 64, 0),
        )

        changeScene(0)
    }

    private fun loop() {
        var beginFrameTime = Time.now
        var dt = -1f

        while (!glfwWindowShouldClose(internalWindow)) {
            // Poll events
            glfwPollEvents()

            DebugDraw.beginFrame()

            frameBuffer?.bind()

            glClearColor(r, g, b, a)
            glClear(GL_COLOR_BUFFER_BIT)

            // Update the current scene only if delta time is equal or greater than 0
            // and the memory for the scene has been allocated.
            if (dt >= 0f) {
                currentScene?.apply {
                    DebugDraw.draw()
                    update(dt)
                    render()
                }
            }

            frameBuffer?.unbind()

            currentScene?.let { imguiLayer?.update(dt, it) }
            glfwSwapBuffers(internalWindow)

            // Calculate delta time
            val endFrameTime = Time.now
            dt = endFrameTime - beginFrameTime
            beginFrameTime = endFrameTime
        }
    }

    private fun freeMemory() {
        // Free the memory allocated by GLFW
        glfwFreeCallbacks(internalWindow)
        glfwDestroyWindow(internalWindow)

        // Terminate GLFW and free the error callback
        glfwTerminate()
        glfwSetErrorCallback(null)?.free()
    }

    fun changeScene(newScene: Int) {
        when (newScene) {
            //0 -> currentScene = LevelEditorScene().also { it.load(); it.start() }
            0 -> currentScene = SceneSerialization.deserialize("level1.json").also { it?.load(); it?.start() }
            1 -> currentScene = LevelScene().also { it.load(); it.start() }
            else -> assert(false) { "Unknown scene $newScene." }
        }
    }
}
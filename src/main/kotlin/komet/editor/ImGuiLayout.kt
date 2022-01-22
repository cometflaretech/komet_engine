package komet.editor

import komet.Window
import komet.scene.Scene
import imgui.*;
import imgui.callback.*
import imgui.flag.*
import imgui.gl3.*
import org.lwjgl.glfw.GLFW.*

class ImGuiLayer(private val glfwWindow: Long) {
    // Mouse cursors provided by GLFW
    private val mouseCursors = LongArray(ImGuiMouseCursor.COUNT)

    // LWJGL3 renderer (SHOULD be initialized)
    private val imGuiGl3 = ImGuiImplGl3()

    // Initialize Dear ImGui.
    fun initImGui() {
        // IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext()

        // ------------------------------------------------------------
        // Initialize ImGuiIO config
        val io: ImGuiIO = ImGui.getIO()
        io.iniFilename = "imgui.ini"
        io.configFlags = ImGuiConfigFlags.NavEnableKeyboard // Navigation with keyboard
        io.backendFlags = ImGuiBackendFlags.HasMouseCursors // Mouse cursors to display while resizing windows etc.
        io.backendPlatformName = "imgui_java_impl_glfw"

        // ------------------------------------------------------------
        // Keyboard mapping. ImGui will use those indices to peek into the io.KeysDown[] array.
        val keyMap = IntArray(ImGuiKey.COUNT)
        keyMap[ImGuiKey.Tab] = GLFW_KEY_TAB
        keyMap[ImGuiKey.LeftArrow] = GLFW_KEY_LEFT
        keyMap[ImGuiKey.RightArrow] = GLFW_KEY_RIGHT
        keyMap[ImGuiKey.UpArrow] = GLFW_KEY_UP
        keyMap[ImGuiKey.DownArrow] = GLFW_KEY_DOWN
        keyMap[ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP
        keyMap[ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN
        keyMap[ImGuiKey.Home] = GLFW_KEY_HOME
        keyMap[ImGuiKey.End] = GLFW_KEY_END
        keyMap[ImGuiKey.Insert] = GLFW_KEY_INSERT
        keyMap[ImGuiKey.Delete] = GLFW_KEY_DELETE
        keyMap[ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE
        keyMap[ImGuiKey.Space] = GLFW_KEY_SPACE
        keyMap[ImGuiKey.Enter] = GLFW_KEY_ENTER
        keyMap[ImGuiKey.Escape] = GLFW_KEY_ESCAPE
        keyMap[ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER
        keyMap[ImGuiKey.A] = GLFW_KEY_A
        keyMap[ImGuiKey.C] = GLFW_KEY_C
        keyMap[ImGuiKey.V] = GLFW_KEY_V
        keyMap[ImGuiKey.X] = GLFW_KEY_X
        keyMap[ImGuiKey.Y] = GLFW_KEY_Y
        keyMap[ImGuiKey.Z] = GLFW_KEY_Z
        io.setKeyMap(keyMap)

        // ------------------------------------------------------------
        // Mouse cursors mapping
        mouseCursors[ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR)
        mouseCursors[ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR)
        mouseCursors[ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR)
        mouseCursors[ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR)
        mouseCursors[ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR)

        // ------------------------------------------------------------
        // GLFW callbacks to handle user input
        glfwSetKeyCallback(glfwWindow) { _: Long, key: Int, _: Int, action: Int, _: Int ->
            if (action == GLFW_PRESS) {
                io.setKeysDown(key, true)
            } else if (action == GLFW_RELEASE) {
                io.setKeysDown(key, false)
            }
            io.keyCtrl = io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL)
            io.keyShift = io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT)
            io.keyAlt = io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT)
            io.keySuper = io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER)
        }
        glfwSetCharCallback(glfwWindow) { _: Long, c: Int ->
            if (c != GLFW_KEY_DELETE) {
                io.addInputCharacter(c)
            }
        }
        glfwSetMouseButtonCallback(glfwWindow) { _: Long, button: Int, action: Int, _: Int ->
            val mouseDown = BooleanArray(5)
            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE
            io.setMouseDown(mouseDown)
            if (!io.wantCaptureMouse && mouseDown[1]) {
                ImGui.setWindowFocus(null)
            }
        }
        glfwSetScrollCallback(glfwWindow) { _: Long, xOffset: Double, yOffset: Double ->
            io.mouseWheelH = io.mouseWheelH + xOffset.toFloat()
            io.mouseWheel = io.mouseWheel + yOffset.toFloat()
        }
        io.setSetClipboardTextFn(object : ImStrConsumer() {
            override fun accept(s: String?) {
                glfwSetClipboardString(glfwWindow, s)
            }
        })
        io.setGetClipboardTextFn(object : ImStrSupplier() {
            override fun get(): String {
                val clipboardString = glfwGetClipboardString(glfwWindow)
                return clipboardString ?: ""
            }
        })

        // Fonts configuration
        val fontAtlas = io.fonts
        val fontConfig = ImFontConfig()
        fontConfig.glyphRanges = fontAtlas.glyphRangesDefault
        fontConfig.pixelSnapH = true
        fontAtlas.addFontFromFileTTF("assets/fonts/segoeui.ttf", 32f, fontConfig)
        fontConfig.destroy()

        // Method initializes LWJGL3 renderer.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.
        imGuiGl3.init("#version 460 core")
    }

    fun update(dt: Float, scene: Scene) {
        startFrame(dt)
        ImGui.newFrame()
        scene.sceneImgui()
        //ImGui.showDemoWindow()
        ImGui.render()
        endFrame()
    }

    private fun startFrame(deltaTime: Float) {
        // Get window properties and mouse position
        val winWidth = floatArrayOf(Window.width.toFloat())
        val winHeight = floatArrayOf(Window.height.toFloat())
        val mousePosX = doubleArrayOf(0.0)
        val mousePosY = doubleArrayOf(0.0)
        glfwGetCursorPos(glfwWindow, mousePosX, mousePosY)

        // We SHOULD call those methods to update Dear ImGui state for the current frame
        val io: ImGuiIO = ImGui.getIO()
        io.setDisplaySize(winWidth[0], winHeight[0])
        io.setDisplayFramebufferScale(1f, 1f)
        io.setMousePos(mousePosX[0].toFloat(), mousePosY[0].toFloat())
        io.deltaTime = deltaTime

        // Update the mouse cursor
        val imguiCursor: Int = ImGui.getMouseCursor()
        glfwSetCursor(glfwWindow, mouseCursors[imguiCursor])
        glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
    }

    private fun endFrame() {
        // After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        imGuiGl3.renderDrawData(ImGui.getDrawData())
    }

    // If you want to clean a room after yourself - do it by yourself
    private fun destroyImGui() {
        imGuiGl3.dispose()
        ImGui.destroyContext()
    }
}
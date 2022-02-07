package komet.editor

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiWindowFlags
import komet.MouseListener
import komet.Window
import komet.util.Vector2

object GameViewWindow {
    private var leftX = 0f
    private var rightX = 0f
    private var topY = 0f
    private var bottomY = 0f

    val wantCaptureMouse
        get() = MouseListener.x in leftX..rightX &&
                MouseListener.y in bottomY..topY

    fun imgui() {
        ImGui.begin(
            "Game Viewport",
            ImGuiWindowFlags.NoScrollbar or ImGuiWindowFlags.NoScrollWithMouse)

        val windowSize = getLargestSizeForViewport()
        val windowPos = getCenteredPositionForViewport(windowSize)

        ImGui.setCursorPos(windowPos.x, windowPos.y)

        val topLeft = ImVec2()
        ImGui.getCursorScreenPos(topLeft)
        topLeft.x -= ImGui.getScrollX()
        topLeft.y -= ImGui.getScrollY()

        leftX = topLeft.x
        bottomY = topLeft.y
        rightX = topLeft.x + windowSize.x
        topY = topLeft.y + windowSize.y

        val textureId = Window.frameBuffer?.texture?.gpuId ?: -1
        ImGui.image(textureId, windowSize.x, windowSize.y, 0f, 1f, 1f, 0f)

        MouseListener.gameViewportPos = Vector2(topLeft.x, topLeft.y)
        MouseListener.gameViewportSize = Vector2(windowSize.x, windowSize.y)

        ImGui.end()
    }

    private fun getLargestSizeForViewport(): ImVec2 {
        val windowSize = ImVec2()
        ImGui.getContentRegionAvail(windowSize)
        windowSize.x -= ImGui.getScrollX()
        windowSize.y -= ImGui.getScrollY()

        var aspectWidth = windowSize.x
        var aspectHeight = aspectWidth / Window.getTargetAspectRatio()
        if (aspectHeight > windowSize.y) {
            // We must switch to pillarbox mode
            aspectHeight = windowSize.y
            aspectWidth = aspectHeight * Window.getTargetAspectRatio()
        }

        return ImVec2(aspectWidth, aspectHeight)
    }

    private fun getCenteredPositionForViewport(aspectSize: ImVec2): ImVec2 {
        val windowSize = ImVec2()
        ImGui.getContentRegionAvail(windowSize)
        windowSize.x -= ImGui.getScrollX()
        windowSize.y -= ImGui.getScrollY()

        val viewportX: Float = windowSize.x / 2.0f - aspectSize.x / 2.0f
        val viewportY: Float = windowSize.y / 2.0f - aspectSize.y / 2.0f

        return ImVec2(
            viewportX + ImGui.getCursorPosX(),
            viewportY + ImGui.getCursorPosY()
        )
    }
}
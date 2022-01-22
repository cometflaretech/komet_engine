package komet

import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

object KeyListener {
    private var keyPressed = BooleanArray(350)

    internal fun keyCallback(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        if (action == GLFW_PRESS) {
            keyPressed[key] = true
        } else if (action == GLFW_RELEASE) {
            keyPressed[key] = false
        }
    }

    fun keyDown(keycode: Int) = keyPressed[keycode]
}
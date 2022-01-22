package komet

import org.joml.Vector4f
import org.lwjgl.glfw.GLFW.GLFW_PRESS
import org.lwjgl.glfw.GLFW.GLFW_RELEASE

object MouseListener {
    private var scrollX = 0.0
    private var scrollY = 0.0
    private var xPos = 0.0
    private var yPos = 0.0
    private var lastX = 0.0
    private var lastY = 0.0
    private var mouseButtonPressed = BooleanArray(8)

    var dragging = false
        private set

    val x: Float
        get() = xPos.toFloat()

    val y: Float
        get() = yPos.toFloat()

    val dx: Float
        get() = (lastX - xPos).toFloat()

    val dy: Float
        get() = (lastY - yPos).toFloat()

    val sx: Float
        get() = scrollX.toFloat()

    val sy: Float
        get() = scrollY.toFloat()

    val ox: Float
        get() {
            val currentX = (x / Window.width) * 2f - 1f
            val tmp = Vector4f(currentX, 0f, 0f, 1f)
            Window.currentScene?.camera?.let {
                tmp.mul(it.inverseProjection).mul(it.inverseView)
            }
            return tmp.x
        }

    val oy: Float
        get() {
            val currentY = (y / Window.height) * 2f - 1f
            val tmp = Vector4f(0f, currentY, 0f, 1f)
            Window.currentScene?.camera?.let {
                tmp.mul(it.inverseProjection).mul(it.inverseView)
            }
            return tmp.y
        }

    fun mouseButtonDown(button: Int) = mouseButtonPressed[button]

    internal fun mousePosCallback(window: Long, xpos: Double, ypos: Double) {
        lastX = xPos
        lastY = yPos
        xPos = xpos
        yPos = ypos
        dragging = mouseButtonPressed[0] || mouseButtonPressed[1] || mouseButtonPressed[2]
    }

    internal fun mouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {
        if (action == GLFW_PRESS) {
            mouseButtonPressed[button] = true
        } else if (action == GLFW_RELEASE) {
            mouseButtonPressed[button] = false
            dragging = !mouseButtonPressed[0] && !mouseButtonPressed[1] && !mouseButtonPressed[2]
        }
    }

    internal fun mouseScrollCallback(window: Long, xOffset: Double, yOffset: Double) {
        scrollX = xOffset
        scrollY = yOffset
    }

    internal fun endFrame() {
        scrollY = 0.0
        scrollY = 0.0
        lastX = xPos
        lastY = yPos
    }
}
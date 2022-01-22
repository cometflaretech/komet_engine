package komet.util

import org.lwjgl.glfw.GLFW.glfwGetTime

object Time {
    val timeStarted = glfwGetTime().toFloat()
    val now: Float
        get() = glfwGetTime().toFloat()
}
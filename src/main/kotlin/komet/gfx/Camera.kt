package komet.gfx

import komet.util.Vector2
import org.joml.Matrix4f
import org.joml.Vector3f

class Camera(var location: Vector2 = Vector2()) {
    private var viewMatrix = Matrix4f()

    var projectionMatrix = Matrix4f()
        private set

    init {
        projectionMatrix.identity()
        projectionMatrix.ortho(0f, 32f * 40f, 0f, 32f * 21f, 0f, 100f)
    }

    val computedViewMatrix: Matrix4f
        get() {
            val cameraFront = Vector3f(0f, 0f, -1f)
            val cameraUp = Vector3f(0f, 1f, 0f)
            viewMatrix.identity()
            viewMatrix.lookAt(
                Vector3f(location.x, location.y, 20f),
                cameraFront.add(location.x, location.y, 0f),
                cameraUp,
            )
            return viewMatrix
        }
}
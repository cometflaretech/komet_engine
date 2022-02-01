package komet.gfx.debug

import komet.Window
import komet.gfx.geometry.Line2D
import komet.util.AssetPool
import komet.util.KMath
import komet.util.Vector2
import komet.util.Vector4
import org.joml.Math
import org.joml.Vector2f
import org.lwjgl.opengl.GL30.*
import java.util.*

// TODO. Add alpha support
object DebugDraw {
    private const val MAX_LINES = 512
    private val lines = mutableListOf<Line2D>()
    private val vertexArray = FloatArray(MAX_LINES * 6 * 2)
    private val shader = AssetPool.getShader("assets/shaders/debugLine2D.glsl")
    private var vaoId: Int = 0
    private var vboId: Int = 0
    private var initialized: Boolean = false

    private fun init() {
        // Generate the VAO
        vaoId = glGenVertexArrays()
        glBindVertexArray(vaoId)

        // Create the VBO and buffer some memory
        vboId = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, vertexArray.size.toLong() * Float.SIZE_BYTES, GL_DYNAMIC_DRAW)

        // Enable the vertex array attributes
        glVertexAttribPointer(
            0,
            3,
            GL_FLOAT,
            false,
            6 * Float.SIZE_BYTES,
            0,
        )
        glEnableVertexAttribArray(0)

        glVertexAttribPointer(
            1,
            3,
            GL_FLOAT,
            false,
            6 * Float.SIZE_BYTES,
            3 * Float.SIZE_BYTES.toLong(),
        )
        glEnableVertexAttribArray(1)

        glLineWidth(2.0f)
        initialized = true
    }

    fun beginFrame() {
        if (!initialized) {
            init()
        }

        run {
            var i = 0
            while (i < lines.size) {
                if (lines[i].beginFrame() < 0) {
                    lines.removeAt(i)
                    i--
                }
                i++
            }
        }
    }

    fun draw() {
        if (lines.size <= 0) {
            return
        }

        var index = 0
        for (line in lines) {
            for (i in 0..1) {
                val position: Vector2 = if (i == 0) line.from else line.to
                val color: Vector4 = line.color

                // Load position
                vertexArray[index] = position.x
                vertexArray[index + 1] = position.y
                vertexArray[index + 2] = -10.0f

                // Load the color
                vertexArray[index + 3] = color.x
                vertexArray[index + 4] = color.y
                vertexArray[index + 5] = color.z
                index += 6
            }
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexArray.copyOfRange(0, lines.size * 6 * 2))

        // Use our shader
        shader?.apply {
            use()

            Window.currentScene?.camera?.apply {
                uploadUniform("uProjection", projectionMatrix)
                uploadUniform("uView", computedViewMatrix)
            }

            // Bind the vao
            glBindVertexArray(vaoId)
            glEnableVertexAttribArray(0)
            glEnableVertexAttribArray(1)

            // Draw the batch
            glDrawArrays(GL_LINES, 0, lines.size * 6 * 2)

            // Disable Location
            glDisableVertexAttribArray(0)
            glDisableVertexAttribArray(1)
            glBindVertexArray(0)

            // Unbind shader
            detach()
        }
    }

    fun addLine2D(from: Vector2, to: Vector2) {
        // TODO: ADD CONSTANTS FOR COMMON COLORS
        addLine2D(from, to, Vector4(0f, 1f, 0f, 1f), 1)
    }

    fun addLine2D(from: Vector2, to: Vector2, color: Vector4) {
        addLine2D(from, to, color, 1)
    }

    fun addLine2D(from: Vector2, to: Vector2, color: Vector4, lifetime: Int) {
        if (lines.size >= MAX_LINES) {
            return
        }
        lines.add(Line2D(from, to, color, lifetime))
    }

    fun addBox2D(center: Vector2, dimensions: Vector2, rotation: Float) {
        // TODO: ADD CONSTANTS FOR COMMON COLORS
        addBox2D(center, dimensions, rotation, Vector4(0f, 1f, 0f, 1f), 1)
    }

    fun addBox2D(center: Vector2, dimensions: Vector2, rotation: Float, color: Vector4) {
        addBox2D(center, dimensions, rotation, color, 1)
    }

    fun addBox2D(
        center: Vector2, dimensions: Vector2, rotation: Float,
        color: Vector4, lifetime: Int
    ) {
        val min = Vector2f(center.x, center.y).sub(Vector2f(dimensions.x, dimensions.y).mul(0.5f))
        val max = Vector2f(center.x, center.y).add(Vector2f(dimensions.x, dimensions.y).mul(0.5f))
        val vertices = arrayOf(
            Vector2(min.x, min.y), Vector2(min.x, max.y),
            Vector2(max.x, max.y), Vector2(max.x, min.y)
        )
        if (rotation != 0.0f) {
            for (vert in vertices) {
                KMath.rotate(vert, rotation, center)
            }
        }
        addLine2D(vertices[0], vertices[1], color, lifetime)
        addLine2D(vertices[0], vertices[3], color, lifetime)
        addLine2D(vertices[1], vertices[2], color, lifetime)
        addLine2D(vertices[2], vertices[3], color, lifetime)
    }

    fun addCircle(center: Vector2, radius: Float) {
        // TODO: ADD CONSTANTS FOR COMMON COLORS
        addCircle(center, radius, Vector4(0f, 1f, 0f, 1f), 1)
    }

    fun addCircle(center: Vector2, radius: Float, color: Vector4) {
        addCircle(center, radius, color, 1)
    }

    fun addCircle(center: Vector2, radius: Float, color: Vector4, lifetime: Int) {
        val points = arrayOfNulls<Vector2>(24)
        val increment = 360 / points.size
        var currentAngle = 0
        for (i in points.indices) {
            val tmp = Vector2(0f, radius)
            KMath.rotate(tmp, currentAngle.toFloat(), Vector2())
            val tmpPoints = Vector2f(tmp.x, tmp.y).add(Vector2f(center.x, center.y))
            points[i] = Vector2(tmpPoints.x, tmpPoints.y)
            if (i > 0) {
                addLine2D(points[i - 1]!!, points[i]!!, color, lifetime)
            }
            currentAngle += increment
        }
        addLine2D(points[points.size - 1]!!, points[0]!!, color, lifetime)
    }
}
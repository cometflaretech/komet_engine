package komet.components

import komet.Window
import komet.ecs.Component
import komet.gfx.debug.DebugDraw
import komet.util.Settings
import komet.util.Vector2
import komet.util.Vector4

class GridLines : Component() {
    override fun update(dt: Float) {
        Window.currentScene?.camera?.let { camera ->
            val firstX = ((camera.location.x / Settings.gridWidth).toInt() - 1) * Settings.gridHeight
            val firstY = ((camera.location.y / Settings.gridHeight).toInt() - 1) * Settings.gridHeight

            val verticalLines = (camera.projectionSize.x / Settings.gridWidth).toInt() + 2
            val horizontalLines = (camera.projectionSize.y / Settings.gridHeight).toInt() + 2

            val height = camera.projectionSize.y.toInt() + Settings.gridHeight * 2
            val width = camera.projectionSize.x.toInt() + Settings.gridWidth * 2

            val maxLines: Int = verticalLines.coerceAtLeast(horizontalLines)
            val color = Vector4(0.2f, 0.2f, 0.2f, 1f)

            for (i in 0 until maxLines) {
                val x = firstX +(Settings.gridWidth * i)
                val y = firstY +(Settings.gridHeight * i)

                if (i < verticalLines) {
                    DebugDraw.addLine2D(
                        Vector2(x.toFloat(), firstY.toFloat()),
                        Vector2(x.toFloat(), (firstY + height).toFloat()),
                        color,
                    )
                }

                if (i < horizontalLines) {
                    DebugDraw.addLine2D(
                        Vector2(firstX.toFloat(), y.toFloat()),
                        Vector2((firstX + width).toFloat(), y.toFloat()),
                        color,
                    )
                }
            }
        }
    }
}
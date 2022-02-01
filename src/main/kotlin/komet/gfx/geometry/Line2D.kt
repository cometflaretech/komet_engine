package komet.gfx.geometry

import komet.util.Vector2
import komet.util.Vector4

class Line2D(var from: Vector2, var to: Vector2, var color: Vector4, var lifetime: Int) {
    fun beginFrame(): Int {
        lifetime--
        return lifetime
    }
}
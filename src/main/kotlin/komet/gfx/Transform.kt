package komet.gfx

import komet.util.Vector2
import kotlinx.serialization.Serializable

@Serializable
data class Transform(
    var location: Vector2 = Vector2(),
    var extent: Vector2 = Vector2(),
    var angle: Float = 0f,
) {
    fun clone() = Transform(
        Vector2(location.x, location.y),
        Vector2(extent.x, extent.y),
        angle
    )

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Transform) return false
        return other.location == location && other.extent == extent && other.angle == angle
    }

    override fun hashCode(): Int {
        var result = location.hashCode()
        result = 31 * result + extent.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }
}
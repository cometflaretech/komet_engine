package komet.gfx

import komet.util.Vector2
import komet.util.Vector4
import kotlinx.serialization.*

@Serializable
data class Sprite(
    @SerialName("a_color")
    val aColor: Vector4 = Vector4(1f),

    @SerialName("a_tex_coords")
    val aTexCoords: Array<Vector2> = arrayOf(
        Vector2(1f, 1f),
        Vector2(1f, 0f),
        Vector2(0f, 0f),
        Vector2(0f, 1f),
    ),

    var texture: String = ""
) {
    //@Transient var texture: Texture? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Sprite

        if (aColor != other.aColor) return false
        if (!aTexCoords.contentEquals(other.aTexCoords)) return false
        if (texture != other.texture) return false

        return true
    }

    override fun hashCode(): Int {
        var result = aColor.hashCode()
        result = 31 * result + aTexCoords.contentHashCode()
        result = 31 * result + texture.hashCode()
        return result
    }
}
package komet.gfx

import komet.util.AssetPool
import komet.util.Vector2

class SpriteSheet(
    var width: Int,
    var height: Int,
    var numSprites: Int,
    var spacing: Int,
) {
    var sprites = mutableListOf<Sprite>()
        private set

    var texture: String = ""
        set(value) {
            for (s in sprites) {
                s.texture = value
            }
            field = value
        }

    fun build() {
        val asset = AssetPool.getTexture(texture) ?: return

        var currentX = 0
        var currentY = asset.height - height
        for (i in 0 until numSprites) {
            val topY = (currentY + height).toFloat() / asset.height
            val rightX = (currentX + width).toFloat() / asset.width
            val leftX = currentX.toFloat() / asset.width
            val bottomY = currentY.toFloat() / asset.height

            val texCoords = arrayOf(
                Vector2(rightX, topY),
                Vector2(rightX, bottomY),
                Vector2(leftX, bottomY),
                Vector2(leftX, topY),
            )

            sprites.add(Sprite(
                aTexCoords = texCoords,
                width = width,
                height = height,
            ).also { it.texture = texture })

            currentX += width + spacing
            if (currentX >= asset.width) {
                currentX = 0
                currentY -= height + spacing
            }
        }
    }
}
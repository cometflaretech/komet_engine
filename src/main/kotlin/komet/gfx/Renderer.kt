package komet.gfx

import komet.components.SpriteRenderer
import komet.ecs.Entity
import komet.util.AssetPool

class Renderer {
    private companion object {
        const val MAX_BATCH_SIZE = 1000
    }
    private val batches = mutableListOf<RenderBatch>()

    fun add(entity: Entity) {
        val spr = entity.getComponent(SpriteRenderer::class.java)
        if (spr != null) {
            add(spr)
        }
    }

    private fun add(spriteRenderer: SpriteRenderer?) {
        var added = false
        for (b in batches) {
            if (b.hasRoom && b.zIndex == spriteRenderer?.entity?.zIndex) {
                val texture = spriteRenderer.sprite?.texture ?: ""
                val asset = AssetPool.getTexture(texture)
                if (asset == null || b.hasTexture(asset) || b.hasTextureRoom) {
                    b.addSprite(spriteRenderer)
                    added = true
                    break
                }
            }
        }
        if (!added) {
            val newBatch = RenderBatch(MAX_BATCH_SIZE).also { it.zIndex = spriteRenderer?.entity?.zIndex ?: 0 }
            newBatch.start()
            batches.add(newBatch)
            newBatch.addSprite(spriteRenderer)
            batches.sort()
        }
    }

    fun render() {
        for (b in batches) {
            b.render()
        }
    }
}
package komet.editor

import komet.components.SpriteRenderer
import komet.ecs.Entity
import komet.gfx.Sprite
import komet.util.Vector2

object Prefabs {
    fun generateSpriteEntity(sprite: Sprite, extent: Vector2) = Entity().apply {
        transform.extent = extent
        addComponent(SpriteRenderer().also { it.sprite = sprite })
    }
}
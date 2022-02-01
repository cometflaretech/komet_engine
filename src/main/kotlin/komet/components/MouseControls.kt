package komet.components

import komet.KeyListener
import komet.MouseListener
import komet.Window
import komet.ecs.Component
import komet.ecs.Entity
import komet.editor.Prefabs
import komet.gfx.Sprite
import komet.util.Settings
import komet.util.Vector2
import org.lwjgl.glfw.GLFW.GLFW_KEY_F
import org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT

class MouseControls : Component() {
    private companion object {
        var counter = 0
    }

    var holdingEntity: Entity? = null
    var holdingSprite: Sprite? = null
    var holdingSpriteSize = 0f

    fun pickUp(sprite: Sprite, spriteSize: Float) {
        val entity = Prefabs.generateSpriteEntity(sprite, Vector2(spriteSize, spriteSize))
        holdingEntity = entity
        holdingSprite = sprite
        holdingSpriteSize = spriteSize
        Window.currentScene?.addEntity(entity, "gen_entity_${counter++}")
    }

    private fun place() {
        holdingEntity = null
        holdingSprite = null
        holdingSpriteSize = 0f
    }

    override fun update(dt: Float) = holdingEntity?.let {
        it.transform.location.x =
            ((MouseListener.ox / Settings.gridWidth).toInt() * Settings.gridWidth).toFloat()
        it.transform.location.y =
            ((MouseListener.oy / Settings.gridHeight).toInt() * Settings.gridHeight).toFloat()

        if (KeyListener.keyDown(GLFW_KEY_F)) {
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                val newEntity =
                    Prefabs.generateSpriteEntity(holdingSprite!!, Vector2(holdingSpriteSize, holdingSpriteSize))
                newEntity.transform.location.x = MouseListener.ox - holdingSpriteSize / 2f
                newEntity.transform.location.y = MouseListener.oy - holdingSpriteSize / 2f
                Window.currentScene?.addEntity(newEntity, "gen_entity_${counter++}")
            }
        } else if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            place()
        }
    }
}
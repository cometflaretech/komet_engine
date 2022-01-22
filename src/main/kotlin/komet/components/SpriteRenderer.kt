package komet.components

import komet.ecs.Component
import komet.gfx.Sprite
import komet.gfx.Transform
import imgui.ImGui
import kotlinx.serialization.*

@Serializable
@SerialName("SpriteRenderer")
class SpriteRenderer : Component() {
    @Required
    var sprite: Sprite? = null
        set(value) {
            if (field != value) {
                field = value
                dirty = true
            }
        }

    @Transient
    private lateinit var lastTransform: Transform

    @Transient
    var dirty = true
        private set

    override fun start() = entity?.let { e ->
        lastTransform = e.transform.clone()
    }

    override fun update(dt: Float) = entity?.let { e ->
        if (lastTransform != e.transform) {
            lastTransform = e.transform.clone()
            dirty = true
        }
    }

    override fun imgui() {
        sprite?.let {
            val imColor = floatArrayOf(it.aColor.x, it.aColor.y, it.aColor.z, it.aColor.w)
            if (ImGui.colorPicker4("Color Picker:", imColor)) {
                sprite?.aColor?.set(imColor[0], imColor[1], imColor[2], imColor[3])
                dirty = true
            }
        }
    }

    fun setClean() {
        dirty = false
    }
}
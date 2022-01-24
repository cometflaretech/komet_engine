package komet.ecs

import komet.gfx.Transform
import kotlinx.serialization.*

@Serializable
class Entity {
    companion object {
        var id_counter = 0
            internal set
    }

    var uuid = -1
        private set

    @Required
    var components = mutableListOf<Component>()
        private set

    var transform: Transform = Transform()

    @SerialName("z_index")
    var zIndex = 0
        set(value) {
            if (value != zIndex) {
                field = value
                // todo. update RenderBatch for real time (+compare())
            }
        }

    init {
        uuid = id_counter++
    }

    fun <T : Component?> getComponent(componentClass: Class<T>): T? {
        for (c in components) {
            if (componentClass.isAssignableFrom(c.javaClass)) {
                try {
                    return componentClass.cast(c)
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                    assert(false) { "Error: Casting component." }
                }
            }
        }
        return null
    }

    fun <T : Component?> removeComponent(componentClass: Class<T>) {
        for (i in components.indices) {
            val c = components[i]
            if (componentClass.isAssignableFrom(c.javaClass)) {
                components.removeAt(i)
                return
            }
        }
    }

    fun addComponent(c: Component): Component {
        c.generateId()
        components.add(c)
        c.entity = this
        return c
    }

    fun start() {
        for (c in components) {
            c.start()
        }
    }

    fun update(dt: Float) {
        for (c in components) {
            c.update(dt)
        }
    }

    fun imgui() {
        for (c in components) {
            c.imgui()
        }
    }
}
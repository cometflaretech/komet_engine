package komet.scene

import komet.ecs.Entity
import komet.gfx.Camera
import komet.gfx.Renderer
import imgui.ImGui
import kotlinx.serialization.*

@Serializable
abstract class Scene {
    @Required
    var entities = mutableMapOf<String, Entity>()
        private set

    @Transient
    var renderer = Renderer()
        private set

    @Transient
    var camera: Camera? = null
        protected set

    @Transient
    var running = false
        private set

    @SerialName("active_entity")
    var activeEntity: String = ""

    open fun load() {}

    open fun update(dt: Float) {
        for (e in entities.values) {
            e.update(dt)
        }
    }

    fun render() = renderer.render()

    fun start() {
        for (e in entities.values) {
            e.start()
            renderer.add(e)
        }
        running = true
    }

    fun addEntity(entity: Entity, name: String): Entity {
        entities[name] = entity
        if (running) {
            entity.start()
            renderer.add(entity)
        }
        return entity
    }

    fun addEntity(name: String) = addEntity(Entity(), name)

    fun sceneImgui() = entities[activeEntity]?.let { e ->
        ImGui.begin("Inspector")
        e.imgui()
        ImGui.end()
    }.also { imgui() }

    open fun imgui(): Unit? = null

    fun prettyPrint() = SceneSerialization.prettyPrint(this)
    fun serialize(filePath: String) = SceneSerialization.serialize(this, filePath)
}
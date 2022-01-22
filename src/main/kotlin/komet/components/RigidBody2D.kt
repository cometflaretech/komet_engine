package komet.components

import komet.ecs.Component
import komet.editor.InspectorProperty
import komet.util.Vector3
import komet.util.Vector4
import kotlinx.serialization.*
import kotlinx.serialization.Transient

@Serializable
@SerialName("RigidBody2D")
class RigidBody2D : Component() {
    @InspectorProperty(0, true)
    private var enabled = true

    @InspectorProperty(1)
    private var colliderType = 0

    @InspectorProperty(2, true)
    private var friction = 0f

    @InspectorProperty(3)
    var velocity = Vector3(0f, .5f, 0f)

    @Transient
    var tmp = Vector4(0f, 0f, 0f, 0f)
}
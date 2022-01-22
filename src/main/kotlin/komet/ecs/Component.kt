package komet.ecs

import komet.editor.InspectorProperty
import komet.util.Vector2
import komet.util.Vector3
import komet.util.Vector4
import imgui.ImGui
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@Serializable
abstract class Component {
    @Transient var entity: Entity? = null

    open fun start(): Unit? = null
    open fun update(dt: Float): Unit? = null

    open fun imgui() {
        try {
            val members = (this::class as KClass<Component>).memberProperties
            members.sortedBy { it.findAnnotation<InspectorProperty>()?.priority }.forEach { member ->
                val inspectorProperty = member.findAnnotation<InspectorProperty>()
                if (inspectorProperty != null && !member.hasAnnotation<Transient>()) {
                    val isPrivate = !member.isAccessible
                    if (isPrivate) {
                        member.isAccessible = true
                    }

                    val type = member.returnType
                    val value = member.get(this)
                    var isReadOnly = inspectorProperty.readOnly
                    var editorName = inspectorProperty.name
                    if (editorName == "") {
                        editorName = member.name.replaceFirstChar {
                            if (it.isLowerCase()) {
                                it.titlecase(Locale.getDefault())
                            } else {
                                it.toString()
                            }
                        }
                        if (type.toString() == "kotlin.Boolean") {
                            editorName = "Is$editorName?"
                        }
                    }

                    when (type.toString()) {
                        "kotlin.Int" -> {
                            val castValue = value as Int
                            val imInt = intArrayOf(castValue)

                            if (isReadOnly) ImGui.beginDisabled()
                            if (ImGui.dragInt(editorName, imInt)) {
                                if (member is KMutableProperty<*>) {
                                    member.setter.call(this, imInt[0])
                                }
                            }
                            if (isReadOnly) ImGui.endDisabled()
                        }
                        "kotlin.Float" -> {
                            val castValue = value as Float
                            val imFloat = floatArrayOf(castValue)

                            if (isReadOnly) ImGui.beginDisabled()
                            if (ImGui.dragFloat(editorName, imFloat)) {
                                if (member is KMutableProperty<*>) {
                                    member.setter.call(this, imFloat[0])
                                }
                            }
                            if (isReadOnly) ImGui.endDisabled()
                        }
                        "kotlin.Boolean" -> {
                            val castValue = value as Boolean

                            if (isReadOnly) ImGui.beginDisabled()
                            if (ImGui.checkbox(editorName, castValue)) {
                                if (member is KMutableProperty<*>) {
                                    member.setter.call(this, !castValue)
                                }
                            }
                            if (isReadOnly) ImGui.endDisabled()
                        }
                        "komet.util.Vector2" -> {
                            val castValue = value as Vector2
                            val imVec2 = floatArrayOf(castValue.x, castValue.y)

                            if (isReadOnly) ImGui.beginDisabled()
                            if (ImGui.dragFloat2(editorName, imVec2)) {
                                castValue.set(imVec2[0], imVec2[1])
                                if (member is KMutableProperty<*>) {
                                    member.setter.call(this, castValue)
                                }
                            }
                            if (isReadOnly) ImGui.endDisabled()
                        }
                        "komet.util.Vector3" -> {
                            val castValue = value as Vector3
                            val imVec3 = floatArrayOf(castValue.x, castValue.y, castValue.z)

                            if (isReadOnly) ImGui.beginDisabled()
                            if (ImGui.dragFloat3(editorName, imVec3)) {
                                castValue.set(imVec3[0], imVec3[1], imVec3[2])
                                if (member is KMutableProperty<*>) {
                                    member.setter.call(this, castValue)
                                }
                            }
                            if (isReadOnly) ImGui.endDisabled()
                        }
                        "komet.util.Vector4" -> {
                            val castValue = value as Vector4
                            val imVec4 = floatArrayOf(castValue.x, castValue.y, castValue.z, castValue.w)

                            if (isReadOnly) ImGui.beginDisabled()
                            if (ImGui.dragFloat4(editorName, imVec4)) {
                                castValue.set(imVec4[0], imVec4[1], imVec4[2], imVec4[3])
                                if (member is KMutableProperty<*>) {
                                    member.setter.call(this, castValue)
                                }
                            }
                            if (isReadOnly) ImGui.endDisabled()
                        }
                    }

                    if (isPrivate) {
                        member.isAccessible = false
                    }
                }
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}
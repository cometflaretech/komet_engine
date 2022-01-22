package komet.editor

annotation class InspectorProperty(
    val priority: Int = 0,
    val readOnly: Boolean = false,
    val name: String = "",
)
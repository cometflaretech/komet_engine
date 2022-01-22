package komet.util

import kotlinx.serialization.Serializable

@Serializable
data class Vector2(
    var x: Float = 0f,
    var y: Float = 0f,
) {
    constructor(all: Float) : this(all, all)

    fun set(all: Float) {
        x = all
        y = all
    }

    fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}

@Serializable
data class Vector3(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
) {
    constructor(all: Float) : this(all, all, all)

    fun set(all: Float) {
        x = all
        y = all
        z = all
    }

    fun set(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }
}

@Serializable
data class Vector4(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var w: Float = 0f,
) {
    constructor(all: Float) : this(all, all, all, all)

    fun set(all: Float) {
        x = all
        y = all
        z = all
        w = all
    }

    fun set(x: Float, y: Float, z: Float, w: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }
}
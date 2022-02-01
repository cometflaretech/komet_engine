package komet.util

import kotlinx.serialization.Serializable
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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

object KMath {
    fun rotate(vec: Vector2, angleDeg: Float, origin: Vector2) {
        val x = vec.x - origin.x
        val y = vec.y - origin.y
        val cos = cos(Math.toRadians(angleDeg.toDouble())).toFloat()
        val sin = sin(Math.toRadians(angleDeg.toDouble())).toFloat()
        var xPrime = x * cos - y * sin
        var yPrime = x * sin + y * cos
        xPrime += origin.x
        yPrime += origin.y
        vec.x = xPrime
        vec.y = yPrime
    }

    fun compare(x: Float, y: Float, epsilon: Float): Boolean {
        return abs(x - y) <= epsilon * 1f.coerceAtLeast(abs(x).coerceAtLeast(abs(y)))
    }

    fun compare(vec1: Vector2, vec2: Vector2, epsilon: Float): Boolean {
        return compare(vec1.x, vec2.x, epsilon) && compare(vec1.y, vec2.y, epsilon)
    }

    fun compare(x: Float, y: Float): Boolean {
        return abs(x - y) <= Float.MIN_VALUE * 1f.coerceAtLeast(abs(x).coerceAtLeast(abs(y)))
    }

    fun compare(vec1: Vector2, vec2: Vector2): Boolean {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y)
    }
}
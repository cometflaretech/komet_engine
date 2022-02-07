package komet.gfx

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage.*

class Texture {
    var width = 0
        private set

    var height = 0
        private set

    var channels = 0
        private set

    var gpuId = -1
        private set

    var filePath = ""

    private var bound = false

    constructor()

    constructor(width_: Int, height_: Int) {
        filePath = "Generated"

        // Generate texture on the GPU and bind it
        gpuId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, gpuId)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGB,
            width_,
            height_,
            0,
            GL_RGB,
            GL_UNSIGNED_BYTE,
            0,
        )
    }


    fun build(filePath: String) {
        this.filePath = filePath

        // Generate texture on the GPU and bind it
        gpuId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, gpuId)

        // Set texture parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        val width = BufferUtils.createIntBuffer(1)
        val height = BufferUtils.createIntBuffer(1)
        val channels = BufferUtils.createIntBuffer(1)

        stbi_set_flip_vertically_on_load(true)

        val image = stbi_load(filePath, width, height, channels, 0)
        if (image != null) {
            this.width = width.get()
            this.height = height.get()
            this.channels = channels.get()

            val channelType = when (this.channels) {
                3 -> GL_RGB
                4 -> GL_RGBA
                else -> {
                    assert(false) {
                        "ERROR<Texture>: Unknown number of channels '${this.channels}' on image '$filePath.'"
                    }
                    0
                }
            }
            glTexImage2D(
                GL_TEXTURE_2D,
                0,
                channelType,
                this.width,
                this.height,
                0,
                channelType,
                GL_UNSIGNED_BYTE,
                image,
            )

            // Free image to avoid memory leak
            stbi_image_free(image)
        } else {
            assert(false) { "ERROR<Texture>: Couldn't load image '$filePath.'" }
        }
    }

    fun bind() {
        if (!bound) {
            glBindTexture(GL_TEXTURE_2D, gpuId)
            bound = true
        }
    }

    fun unbind() {
        if (bound) {
            glBindTexture(GL_TEXTURE_2D, 0)
            bound = false
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Texture) return false
        return other.width == this.width && other.height == this.height && other.gpuId == this.gpuId &&
                other.filePath == this.filePath
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + channels
        result = 31 * result + gpuId
        result = 31 * result + filePath.hashCode()
        result = 31 * result + bound.hashCode()
        return result
    }
}
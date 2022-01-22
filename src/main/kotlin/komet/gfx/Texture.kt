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

    private var gpuId = 0
    private var bound = false

    fun build(filePath: String) {
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
}
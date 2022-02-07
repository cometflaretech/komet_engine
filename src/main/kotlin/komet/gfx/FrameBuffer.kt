package komet.gfx

import org.lwjgl.opengl.GL30.*

class FrameBuffer(val width: Int, val height: Int) {
    var gpuId = -1
        private set

    var texture: Texture? = null
        private set

    init {
        // Generate framebuffer
        gpuId = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, gpuId)

        // Create the texture and attach it to the frame buffer
        texture = Texture(width, height).also {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, it.gpuId, 0)
        }

        // Create the render buffer to store the depth buffer
        val rboId = glGenRenderbuffers()
        glBindRenderbuffer(GL_RENDERBUFFER, rboId)
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height)
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboId)

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert(false) { "Frame Buffer is not complete" }
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, gpuId)
    }

    fun unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }
}
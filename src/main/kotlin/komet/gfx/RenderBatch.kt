package komet.gfx

import komet.Window
import komet.components.SpriteRenderer
import komet.util.AssetPool
import org.lwjgl.opengl.GL30.*

class RenderBatch(private val maxBatchSize: Int) : Comparable<RenderBatch> {
    private companion object {
        const val POS_SIZE = 2
        const val COLOR_SIZE = 4
        const val TEX_COORDS_SIZE = 2
        const val TEX_ID_SIZE = 1

        const val POS_OFFSET = 0
        const val COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.SIZE_BYTES
        const val TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.SIZE_BYTES
        const val TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.SIZE_BYTES
        const val VERTEX_SIZE = 9
        const val VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.SIZE_BYTES
    }

    private var spriteRenderers = arrayOfNulls<SpriteRenderer>(maxBatchSize)
    private var numSprites = 0
    private var vertices = FloatArray(maxBatchSize * 4 * VERTEX_SIZE)
    private val texSlots = arrayOf(0, 1, 2, 3, 4, 5, 6, 7)

    private var textures = mutableListOf<Texture>()
    private var vaoID = 0
    private var vboID = 0
    private var shader = AssetPool.getShader("assets/shaders/default.glsl")

    var hasRoom = true
        private set

    val hasTextureRoom
        get() = textures.size < 8

    var zIndex = 0

    fun start() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays()
        glBindVertexArray(vaoID)

        // Allocate space for vertices
        vboID = glGenBuffers()
        glBindBuffer(GL_ARRAY_BUFFER, vboID)
        glBufferData(GL_ARRAY_BUFFER, vertices.size.toLong() * Float.SIZE_BYTES, GL_DYNAMIC_DRAW)

        // Create and upload indices buffer
        val eboID: Int = glGenBuffers()
        val indices: IntArray = generateIndices()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        // Enable vertex attribute pointers
        glVertexAttribPointer(
            0,
            POS_SIZE, GL_FLOAT,
            false,
            VERTEX_SIZE_BYTES,
            POS_OFFSET.toLong(),
        )
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(
            1,
            COLOR_SIZE,
            GL_FLOAT,
            false,
            VERTEX_SIZE_BYTES,
            COLOR_OFFSET.toLong(),
        )
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(
            2,
            TEX_COORDS_SIZE,
            GL_FLOAT,
            false,
            VERTEX_SIZE_BYTES,
            TEX_COORDS_OFFSET.toLong(),
        )
        glEnableVertexAttribArray(2)
        glVertexAttribPointer(
            3,
            TEX_ID_SIZE,
            GL_FLOAT,
            false,
            VERTEX_SIZE_BYTES,
            TEX_ID_OFFSET.toLong(),
        )
        glEnableVertexAttribArray(3)
    }

    fun addSprite(spriteRenderer: SpriteRenderer?) {
        // Get index and add renderObject
        val index = numSprites
        spriteRenderers[index] = spriteRenderer
        numSprites++

        /*if (spriteRenderer?.sprite?.texture != null) {
            if (!textures.contains(spriteRenderer.sprite!!.texture)) {
                textures.add(spriteRenderer.sprite!!.texture!!)
            }
        }*/

        spriteRenderer?.sprite?.let {
            if (it.texture != "") {
                val asset = AssetPool.getTexture(it.texture)
                if (asset != null && !textures.contains(asset)) {
                    textures.add(asset)
                }
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index)

        if (numSprites >= maxBatchSize) {
            hasRoom = false
        }
    }

    fun render() {
        var reBufferData = false
        for (i in 0 until numSprites) {
            val sr = spriteRenderers[i]
            if (sr?.dirty == true) {
                loadVertexProperties(i)
                sr.setClean()
                reBufferData = true
            }
        }

        if (reBufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID)
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices)
        }

        shader?.apply {
            use()
            Window.currentScene?.camera?.apply {
                uploadUniform("uProjection", projectionMatrix)
                uploadUniform("uView", computedViewMatrix)

                for (i in textures.indices) {
                    glActiveTexture(GL_TEXTURE0 + i + 1)
                    textures[i].bind()
                }

                uploadUniform("uTextures", texSlots.toIntArray())
            }
        }

        glBindVertexArray(vaoID)
        glEnableVertexAttribArray(0)
        glEnableVertexAttribArray(1)
        glDrawElements(GL_TRIANGLES, numSprites * 6, GL_UNSIGNED_INT, 0)
        glDisableVertexAttribArray(0)
        glDisableVertexAttribArray(1)
        glBindVertexArray(0)

        for (t in textures) {
            t.unbind()
        }

        shader?.detach()
    }

    private fun loadVertexProperties(index: Int) {
        val spriteRenderer = spriteRenderers[index]

        spriteRenderer?.sprite?.let {
            // Find offset within array (4 vertices per sprite)
            var offset = index * 4 * VERTEX_SIZE

            val color = it.aColor
            val texCoords = it.aTexCoords

            var texId = 0

            val asset = AssetPool.getTexture(it.texture)
            for (i in textures.indices) {
                if (textures[i] == asset) {
                    texId = i + 1
                    break
                }
            }

            // Add vertices with the appropriate properties
            var xAdd = 1.0f
            var yAdd = 1.0f
            for (i in 0..3) {
                when (i) {
                    1 -> yAdd = 0.0f
                    2 -> xAdd = 0.0f
                    3 -> yAdd = 1.0f
                }

                // Load position
                spriteRenderer.entity?.transform?.apply {
                    vertices[offset] = location.x + xAdd * extent.x
                    vertices[offset + 1] = location.y + yAdd * extent.y
                }

                // Load color
                vertices[offset + 2] = color.x
                vertices[offset + 3] = color.y
                vertices[offset + 4] = color.z
                vertices[offset + 5] = color.w

                // Load texture coordinates
                vertices[offset + 6] = texCoords[i].x
                vertices[offset + 7] = texCoords[i].y

                // Load texture id
                vertices[offset + 8] = texId.toFloat()

                offset += VERTEX_SIZE
            }
        }

    }

    private fun generateIndices(): IntArray {
        // 6 indices per quad (3 per triangle)
        val elements = IntArray(6 * maxBatchSize)
        for (i in 0 until maxBatchSize) {
            loadElementIndices(elements, i)
        }
        return elements
    }

    private fun loadElementIndices(elements: IntArray, index: Int) {
        val offsetArrayIndex = 6 * index
        val offset = 4 * index

        // 3, 2, 0, 0, 2, 1        7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = offset + 3
        elements[offsetArrayIndex + 1] = offset + 2
        elements[offsetArrayIndex + 2] = offset + 0

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0
        elements[offsetArrayIndex + 4] = offset + 2
        elements[offsetArrayIndex + 5] = offset + 1
    }

    fun hasTexture(texture: Texture) = textures.contains(texture)

    override fun compareTo(other: RenderBatch) = zIndex.compareTo(other.zIndex)
}
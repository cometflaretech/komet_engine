package komet.util

import komet.gfx.Shader
import komet.gfx.SpriteSheet
import komet.gfx.Texture
import java.io.File

object AssetPool {
    private var shaders = mutableMapOf<String, Shader>()
    private var textures = mutableMapOf<String, Texture>()
    private var spriteSheets = mutableMapOf<String, SpriteSheet>()

    fun getShader(resourceName: String): Shader? {
        val file = File(resourceName)
        return if (shaders.containsKey(resourceName)) {
            shaders[resourceName]
        } else {
            val shader = Shader(resourceName).also { it.compile() }
            shaders[resourceName] = shader
            shader
        }
    }

    fun getTexture(resourceName: String): Texture? {
        val file = File(resourceName)
        return if (textures.containsKey(resourceName)) {
            textures[resourceName]
        } else {
            val texture = Texture().apply { build(resourceName) }
            textures[resourceName] = texture
            texture
        }
    }

    fun addSpriteSheet(resourceName: String, spriteSheet: SpriteSheet): SpriteSheet {
        val file = File(resourceName)
        if (!spriteSheets.containsKey(resourceName)) {
            spriteSheets[resourceName] = spriteSheet
            spriteSheet.texture = resourceName
            spriteSheet.build()
        }
        return spriteSheet
    }

    fun getSpriteSheet(resourceName: String): SpriteSheet? {
        val file = File(resourceName)
        if (spriteSheets.containsKey(resourceName)) {
            assert(false) { "Error<AssetPool>: You are trying to access not found $resourceName sprite sheet" }
        }
        return spriteSheets.getOrDefault(resourceName, null)
    }
}
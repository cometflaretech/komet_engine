package komet

import komet.components.SpriteRenderer
import komet.ecs.Entity
import komet.scene.Scene
import komet.gfx.Camera
import komet.gfx.Sprite
import komet.util.AssetPool
import komet.util.Vector2
import imgui.ImGui
import imgui.ImVec2
import kotlinx.serialization.*
import org.lwjgl.glfw.GLFW

@Serializable
@SerialName("LevelEditorScene")
class LevelEditorScene : Scene() {
    @Transient private lateinit var blueStar: Entity

    override fun load() {
        /*blueStar = addEntity("BlueStar").also { e ->
            e.transform.apply {
                location = Vector2(100f, 100f)
                extent = Vector2(128f, 128f)
            }
            e.addComponent(SpriteRenderer().also { c ->
                c.sprite = AssetPool.getSpriteSheet("assets/textures/stars.png")?.getSprite(1)
            })
        }

        addEntity("Square").also { e ->
            e.transform.apply {
                location = Vector2(200f, 400f)
                extent = Vector2(128f, 128f)
            }
            e.zIndex = 1
            e.addComponent(SpriteRenderer().also { c ->
                c.sprite = Sprite()
            })
        }*/

        /*addEntity("Green2").also { e ->
            e.transform.apply {
                location = Vector2(400f, 400f)
                extent = Vector2(128f, 128f)
            }
            e.zIndex = 1
            e.addComponent(SpriteRenderer().also { c ->
                c.sprite = Sprite().also { s ->
                    s.texture = "assets/textures/p2.png"
                }
            }) as SpriteRenderer?
        }*/

        camera = Camera(Vector2(-250f, 0f))

        //serialize("level1.json")
    }

    override fun update(dt: Float) {
        //blueStar.transform.location.x = 450f

        //if (KeyListener.keyDown(GLFW_KEY_SPACE)) { // todo. realtime update zIndex
        //    blueStar.zIndex = -blueStar.zIndex
        //}

        if (KeyListener.keyDown(GLFW.GLFW_KEY_M)) {
            serialize("level1.json")
        }

        //serialize("level1.json")
    }

    override fun imgui() {
        ImGui.begin("Palette")

        val windowPos = ImVec2()
        ImGui.getWindowPos(windowPos)
        val windowSize = ImVec2()
        ImGui.getWindowSize(windowSize)
        val itemSpacing = ImVec2()
        ImGui.getStyle().getItemSpacing(itemSpacing)

        val windowX2 = windowPos.x + windowSize.x

        val sprites = AssetPool.getSpriteSheet("assets/textures/stars.png")?.sprites
        sprites?.let {
            for ((i, sprite) in it.withIndex()) {
                val spriteSize = 64f
                val id = AssetPool.getTexture(sprite.texture)?.gpuId ?: -1
                val texCoords = sprite.aTexCoords

                ImGui.pushID(i)
                if (ImGui.imageButton(
                        id,
                        spriteSize,
                        spriteSize,
                        texCoords[0].x,
                        texCoords[0].y,
                        texCoords[2].x,
                        texCoords[2].y,
                    )) {
                    println("Button $i clicked")
                }
                ImGui.popID()

                val lastButtonPos = ImVec2()
                ImGui.getItemRectMax(lastButtonPos)
                val lastButtonX2 = lastButtonPos.x
                val nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteSize

                if (i + 1 < sprites.size && nextButtonX2 < windowX2) {
                    ImGui.sameLine()
                }
            }
        }

        ImGui.end()
    }
}
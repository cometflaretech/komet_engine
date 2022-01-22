package komet.util

import komet.LevelEditorScene
import komet.LevelScene
import komet.components.*
import komet.ecs.Component
import komet.scene.Scene
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*

val module = SerializersModule {
    polymorphic(Component::class) {
        subclass(SpriteRenderer::class)
        subclass(FontRenderer::class)
        subclass(RigidBody2D::class)
        subclass(Collider2D::class)
    }
    polymorphic(Scene::class) {
        subclass(LevelEditorScene::class)
        subclass(LevelScene::class)
    }
}

@OptIn(ExperimentalSerializationApi::class)
val json = Json {
    serializersModule = module
    prettyPrint = true
    prettyPrintIndent = "  "
}
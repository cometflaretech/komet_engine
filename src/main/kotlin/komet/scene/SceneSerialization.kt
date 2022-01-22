package komet.scene

import komet.util.json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths

object SceneSerialization {
    fun deserialize(filePath: String): Scene? {
        return try {
            val inFile = String(Files.readAllBytes(Paths.get(filePath)))
            val scene = json.decodeFromString<Scene>(inFile)
            for (e in scene.entities.values) {
                for (c in e.components) {
                    c.entity = e
                }
            }
            scene
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun serialize(scene: Scene, filePath: String): Boolean {
        return try {
            val fileWriter = FileWriter(filePath)
            fileWriter.write(json.encodeToString(scene))
            fileWriter.close()
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun prettyPrint(scene: Scene) {
        println(json.encodeToString(scene))
    }
}
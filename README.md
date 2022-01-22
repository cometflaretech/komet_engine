# Komet Engine

#### Supported Platforms
* Windows 10, 11 with JVM installed
* Linux JVM - not tested
* Other Operating Systems are planned int the future*

#### Supported Rendering Backends
* OpenGL 4.6
* Vulkan is planned in the future*

#### Supported Audio Backends
* Currently none

#### Key Features
* 2D scene builder and management
* Serialization and deserialization made easy
* Inspectable code in the editor using reflection
* Mouse and Keyboard input events
* Entity-Component based (Systems are planned in the future*)
* Support for adding custom shaders and textures
* Batch rendering for maximum performance
* Easy to code, easy to deploy, easy to maintain

### Example of defining a component
```kotlin
@Serializable
@SerialName("RigidBody2D")
class RigidBody2D : Component() {
    @InspectorProperty(priority = 0, true)
    private var enabled = true

    @InspectorProperty(priority = 1)
    private var colliderType = 0

    @InspectorProperty(priority = 2, true)
    private var friction = 0f

    @InspectorProperty(priority = 3, name = "CustomName")
    var velocity = Vector3(0f, .5f, 0f)

    @Transient
    var tmp = Vector4(0f, 0f, 0f, 0f)
}
```

### Build and run the project
```
Clone the repository
Open it with IntelliJ IDEA
Make sure you have Kotlin 1.6.10 version installed
Also make sure you have JDK 1.8 installed
Go to the src/main/kotlin/Main.kt and open the file
Press the green arrow next to main function
```

#### License
Licensed under <b>Apache License 2.0<b>
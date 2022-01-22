package komet.gfx

import org.joml.*
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL20.*
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths


class Shader(private val filepath: String) {
    var gpuID = 0
        private set

    private lateinit var vertexSource: CharSequence
    private lateinit var fragmentSource: CharSequence
    private var beingUsed = false

    init {
        try {
            val source = String(Files.readAllBytes(Paths.get(filepath)))
            val splitString = source.splitToSequence("(#type)( )+([a-zA-Z]+)".toRegex())

            // Find the first pattern after #type 'pattern'
            var index = source.indexOf("#type") + 6
            var eol = source.indexOf("\r\n", index)
            val firstPattern = source.substring(index, eol).trim { it <= ' ' }

            // Find the second pattern after #type 'pattern'
            index = source.indexOf("#type", eol) + 6
            eol = source.indexOf("\r\n", index)
            val secondPattern = source.substring(index, eol).trim { it <= ' ' }
            when (firstPattern) {
                "vertex" -> vertexSource = splitString.elementAt(1)
                "fragment" -> fragmentSource = splitString.elementAt(1)
                else -> throw IOException("Unexpected token '$firstPattern'")
            }
            when (secondPattern) {
                "vertex" -> vertexSource = splitString.elementAt(2)
                "fragment" -> fragmentSource = splitString.elementAt(2)
                else -> throw IOException("Unexpected token '$secondPattern'")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            assert(false) { "Error: Could not open file for shader: '$filepath'" }
        }
    }

    fun compile() {
        // First load and compile the vertex shader
        val gpuVertexID = glCreateShader(GL_VERTEX_SHADER)
        // Pass the shader source to the GPU
        glShaderSource(gpuVertexID, vertexSource)
        glCompileShader(gpuVertexID)

        // Check for errors during compilation
        var success = glGetShaderi(gpuVertexID, GL_COMPILE_STATUS)
        if (success == GL_FALSE) {
            val len = glGetShaderi(gpuVertexID, GL_INFO_LOG_LENGTH)
            println("ERROR: '$filepath'\n\tVertex shader compilation failed.")
            println(glGetShaderInfoLog(gpuVertexID, len))
            assert(false) { "" }
        }

        // First load and compile the vertex shader
        val gpuFragmentID = glCreateShader(GL_FRAGMENT_SHADER)
        // Pass the shader source to the GPU
        glShaderSource(gpuFragmentID, fragmentSource)
        glCompileShader(gpuFragmentID)

        // Check for errors in compilation
        success = glGetShaderi(gpuFragmentID, GL_COMPILE_STATUS)
        if (success == GL_FALSE) {
            val len = glGetShaderi(gpuFragmentID, GL_INFO_LOG_LENGTH)
            val msg = "ERROR: '$filepath'\n\tFragment shader compilation failed."
            println(msg)
            println(glGetShaderInfoLog(gpuFragmentID, len))
            assert(false) { msg }
        }

        // Link shaders and check for errors
        gpuID = glCreateProgram()
        glAttachShader(gpuID, gpuVertexID)
        glAttachShader(gpuID, gpuFragmentID)
        glLinkProgram(gpuID)

        // Check for linking errors
        success = glGetProgrami(gpuID, GL_LINK_STATUS)
        if (success == GL_FALSE) {
            val len = glGetProgrami(gpuID, GL_INFO_LOG_LENGTH)
            val msg = "ERROR: '$filepath'\n\tLinking of shaders failed."
            println(msg)
            println(glGetProgramInfoLog(gpuID, len))
            assert(false) { msg }
        }
    }

    fun use() {
        if (!beingUsed) {
            // Bind shader program
            glUseProgram(gpuID)
            beingUsed = true
        }
    }

    fun detach() {
        glUseProgram(0)
        beingUsed = false
    }

    fun uploadUniform(varName: CharSequence, bool: Boolean) {
        val varLocation = glGetUniformLocation(gpuID, varName)
        use()
        glUniform1i(varLocation, if (bool) 1 else 0)
    }

    fun uploadUniform(varName: CharSequence, scalar: Int) {
        val varLocation = glGetUniformLocation(gpuID, varName)
        use()
        glUniform1i(varLocation, scalar)
    }

    fun uploadUniform(varName: CharSequence, scalar: Float) {
        val varLocation = glGetUniformLocation(gpuID, varName)
        use()
        glUniform1f(varLocation, scalar)
    }

    fun uploadUniform(varName: CharSequence, array: IntArray) {
        val varLocation = glGetUniformLocation(gpuID, varName)
        use()
        glUniform1iv(varLocation, array)
    }

    fun uploadUniform(varName: CharSequence, array: FloatArray) {
        val varLocation = glGetUniformLocation(gpuID, varName)
        use()
        glUniform1fv(varLocation, array)
    }

    fun uploadUniform(varName: CharSequence, vec2: Vector2f) {
        val varLocation = glGetUniformLocation(gpuID, varName)
        val vecBuffer = BufferUtils.createFloatBuffer(2)
        vec2.get(vecBuffer)
        use()
        glUniform2fv(varLocation, vecBuffer)
    }

    fun uploadUniform(varName: CharSequence, vec3: Vector3f) {
        val varLocation = glGetUniformLocation(gpuID, varName)
        val vecBuffer = BufferUtils.createFloatBuffer(3)
        vec3.get(vecBuffer)
        use()
        glUniform3fv(varLocation, vecBuffer)
    }

    fun uploadUniform(varName: CharSequence, vec4: Vector4f) {
        val varLocation = glGetUniformLocation(gpuID, varName)
        val vecBuffer = BufferUtils.createFloatBuffer(4)
        vec4.get(vecBuffer)
        use()
        glUniform4fv(varLocation, vecBuffer)
    }

    fun uploadUniform(varName: CharSequence, mat2: Matrix2f) {
        val varLocation = glGetUniformLocation(gpuID, varName)
        val matBuffer = BufferUtils.createFloatBuffer(4)
        mat2.get(matBuffer)
        use()
        glUniformMatrix2fv(varLocation, false, matBuffer)
    }

    fun uploadUniform(varName: CharSequence, mat3: Matrix3f) {
        val varLocation = glGetUniformLocation(gpuID, varName)
        val matBuffer = BufferUtils.createFloatBuffer(9)
        mat3.get(matBuffer)
        use()
        glUniformMatrix3fv(varLocation, false, matBuffer)
    }

    fun uploadUniform(varName: CharSequence, mat4: Matrix4f) {
        val varLocation = glGetUniformLocation(gpuID, varName)
        val matBuffer = BufferUtils.createFloatBuffer(16)
        mat4.get(matBuffer)
        use()
        glUniformMatrix4fv(varLocation, false, matBuffer)
    }
}
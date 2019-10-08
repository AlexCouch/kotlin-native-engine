import glew.*
import kotlinx.cinterop.*
import platform.posix.*

data class ShaderProgramSource(val vertexSource: String, val fragmentSource: String)

enum class ShaderType(val type: Int){
    VERTEX(0),
    FRAGMENT(1),
    NONE(2)

}

abstract class ShaderProgram{
    val program = glCreateProgram?.invoke() ?: throw Exception("Could not create shader program")

    @ExperimentalUnsignedTypes
    fun compileShader(shaderProgramSource: ShaderProgramSource): Pair<UInt, UInt>{
        val fragmentSource = shaderProgramSource.fragmentSource
        val vertexSource = shaderProgramSource.vertexSource
        println("Created shader")
        memScoped{
            val fragmentShaderID = glCreateShader?.invoke(GL_FRAGMENT_SHADER.convert()) ?: throw Exception("Could not create shader")
            createShadersAndValidate(fragmentShaderID, fragmentSource)
            val vertexShaderID = glCreateShader?.invoke(GL_VERTEX_SHADER.convert()) ?: throw Exception("Could not create shader")
            createShadersAndValidate(vertexShaderID, vertexSource)
            glAttachShader(program, fragmentShaderID)
            glAttachShader(program, vertexShaderID)
            glLinkProgram?.invoke(program) ?: throw Exception("Could not link program")
            glValidateProgram?.invoke(program) ?: throw Exception("Could not validate program")
            return (fragmentShaderID to vertexShaderID)
        }
    }

    @ExperimentalUnsignedTypes
    private fun createShadersAndValidate(id: UInt, shaderSource: String){
        memScoped {
            val src = cValuesOf(shaderSource.cstr.ptr).ptr
            glShaderSource?.invoke(id, 1, src, null) ?: throw Exception("Could not set shader source")
            println("Shader source set")
            glCompileShader?.invoke(id) ?: throw Exception("Could not compile shader")
            println("Shader done compiling")
            val result = alloc<IntVar>()
            glGetShaderiv?.invoke(id, GL_COMPILE_STATUS.convert(), result.ptr.reinterpret())
            println(result.value)
            if (result.value == GL_FALSE) {
                val length = alloc<IntVar>()
                glGetShaderiv?.invoke(id, GL_INFO_LOG_LENGTH.convert(), length.ptr.reinterpret())
                val lengthValue = length.value
                val message = allocArray<ByteVar>(lengthValue * sizeOf<ByteVar>())
                glGetShaderInfoLog?.invoke(id, lengthValue, length.ptr.reinterpret(), message)
                println("Failed to compile shader.")
                println(message.toKString())
                glDeleteShader?.invoke(id)
            }
            println("Compile status: OK")

        }
    }

    @ExperimentalUnsignedTypes
    fun compileShaderFromFile(shaderName: String): Pair<UInt, UInt>{
        val path = "src/mingwMain/resources/shaders/$shaderName.shader"
        val shaderFile = fopen(path, "r") ?: throw Exception("File with name $path not found.")
        fseek(shaderFile, 0, SEEK_END)
        val filelen = ftell(shaderFile)
        rewind(shaderFile)

        memScoped {
            val shaderSource = allocArray<ByteVar>(filelen+1)
            fread(shaderSource, filelen.convert(), 1u, shaderFile)
            val sourceStr = shaderSource.toKString()
            val ss = arrayOf(/* Fragment Shader */StringBuilder(), /* Vertex Shader */ StringBuilder())
            var shaderType = ShaderType.NONE.type
            sourceStr.lines().forEach {
                if(it.startsWith("#shader")){
                    println("FRAGMENT")
                    shaderType =
                        when {
                            it.contains("fragment") -> ShaderType.FRAGMENT.type
                            it.contains("vertex") -> ShaderType.VERTEX.type
                            else -> ShaderType.NONE.type
                        }
                }else{
                    println(it)
                    ss[shaderType].append(it).append('\n')
                }
            }
            return compileShader(ShaderProgramSource(ss[ShaderType.VERTEX.type].toString(), ss[ShaderType.FRAGMENT.type].toString()))
        }
    }

    fun setUniformf(uniformName: String, value: Float){
        val byteBuf = nativeHeap.allocArray<ByteVar>(uniformName.length){ index ->
            this.value = uniformName[index].toByte()
        }
        glUniform1f?.invoke(glGetUniformLocation?.invoke(program, byteBuf) ?: throw IllegalStateException("No uniform of that name"), value)
    }

    fun setUniformi(uniformName: String, value: Int){
        val byteBuf = nativeHeap.allocArray<ByteVar>(uniformName.length){ index ->
            this.value = uniformName[index].toByte()
        }
        glUniform1i?.invoke(glGetUniformLocation?.invoke(program, byteBuf) ?: throw IllegalStateException("No uniform of that name"), value)
    }

    fun setUniform4i(uniformName: String, v1: Int, v2: Int, v3: Int, v4: Int){
        val byteBuf = nativeHeap.allocArray<ByteVar>(uniformName.length){ index ->
            this.value = uniformName[index].toByte()
        }
        glUniform4i?.invoke(glGetUniformLocation?.invoke(program, byteBuf) ?: throw IllegalStateException("No uniform of that name"), v1, v2, v3, v4)
    }

    fun setUniform4f(uniformName: String, v1: Float, v2: Float, v3: Float, v4: Float){
        val byteBuf = nativeHeap.allocArray<ByteVar>(uniformName.length){ index ->
            this.value = uniformName[index].toByte()
        }
        glUniform4f?.invoke(glGetUniformLocation?.invoke(program, byteBuf) ?: throw IllegalStateException("No uniform of that name"), v1, v2, v3, v4)
    }

    fun startProgram(){
        glUseProgram?.invoke(program) ?: throw Exception("Could not use shader program")
    }

    @ExperimentalUnsignedTypes
    fun stopProgram(){
        glUseProgram?.invoke(0u.convert())
    }

    @ExperimentalUnsignedTypes
    fun deleteShader(shaderId: UInt){
        glDeleteShader(shaderId)
    }

    abstract fun cleanup()
}

@ExperimentalUnsignedTypes
abstract class BasicShader : ShaderProgram(){
    abstract val shaderSource: Pair<UInt, UInt>

    override fun cleanup() {
        this.deleteShader(shaderSource.first)
        this.deleteShader(shaderSource.second)
    }

}

@ExperimentalUnsignedTypes
object ColoredShader : BasicShader(){
    override val shaderSource = this.compileShaderFromFile("colored")

    override fun cleanup() {
        this.deleteShader(shaderSource.first)
        this.deleteShader(shaderSource.second)
    }
}

@ExperimentalUnsignedTypes
object TexturedShader : ShaderProgram(){
    private val shaderSource: Pair<UInt, UInt> = this.compileShaderFromFile("textured")

    override fun cleanup() {
        this.deleteShader(shaderSource.first)
        this.deleteShader(shaderSource.second)
    }


}





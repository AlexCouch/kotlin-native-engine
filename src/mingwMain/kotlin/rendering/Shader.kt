import glew.*
import kotlinx.cinterop.*
import platform.posix.*

abstract class ShaderProgram{
    val program = glCreateProgram?.invoke() ?: throw Exception("Could not create shader program")

    @ExperimentalUnsignedTypes
    fun compileShader(source: String, shaderType: Int): UInt{
        val id = glCreateShader?.invoke(shaderType.convert()) ?: throw Exception("Could not create shader")
        println("Created shader")
        memScoped{
            val src = cValuesOf(source.cstr.ptr).ptr
            glShaderSource?.invoke(id, 1, src, null) ?: throw Exception("Could not set shader source")
            println("Shader source set")
            glCompileShader?.invoke(id) ?: throw Exception("Could not compile shader")
            println("Shader done compiling")
            val result = alloc<IntVar>()
            glGetShaderiv?.invoke(id, GL_COMPILE_STATUS.convert(), result.ptr.reinterpret())
            println(result.value)
            if(result.value == GL_FALSE){
                val length = alloc<IntVar>()
                glGetShaderiv?.invoke(id, GL_INFO_LOG_LENGTH.convert(), length.ptr.reinterpret())
                val lengthValue = length.value
                val message = allocArray<ByteVar>(lengthValue * sizeOf<ByteVar>())
                glGetShaderInfoLog?.invoke(id, lengthValue, length.ptr.reinterpret(), message)
                println("Failed to compile ${if(shaderType == GL_VERTEX_SHADER) "vertex" else "fragment"}")
                println(message.toKString())
                glDeleteShader?.invoke(id)
                return 0u
            }
            println("Compile status: OK")
        }
        return id
    }

    @ExperimentalUnsignedTypes
    fun compileShaderFromFile(shaderName: String, shaderType: Int): UInt{
        val path = if(shaderType == GL_FRAGMENT_SHADER) "src/mingwMain/resources/shaders/$shaderName.frag" else "src/mingwMain/resources/shaders/$shaderName.vert"
        val shaderFile = fopen(path, "r") ?: throw Exception("File with name $path not found.")
        fseek(shaderFile, 0, SEEK_END)
        val filelen = ftell(shaderFile)
        rewind(shaderFile)

        memScoped {
            val shaderSource = allocArray<ByteVar>(filelen+1)
            fread(shaderSource, filelen.convert(), 1u, shaderFile)
            val sourceStr = shaderSource.toKString()
            return compileShader(sourceStr, shaderType)
        }
    }

    protected abstract fun bindAttributes()

    @ExperimentalUnsignedTypes
    protected fun bindAttribute(attribute: UInt, variableName: String){
        val byteBuffer = nativeHeap.allocArray<ByteVar>(variableName.length){index ->
            this.value = variableName[index].toByte()
        }
        glBindAttribLocation?.invoke(this.program, attribute.convert(), byteBuffer)
    }

    fun startProgram(){
        glLinkProgram?.invoke(program) ?: throw Exception("Could not link program")
        glValidateProgram?.invoke(program) ?: throw Exception("Could not validate program")
        glUseProgram?.invoke(program) ?: throw Exception("Could not use shader program")
        this.bindAttributes()
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

    @ExperimentalUnsignedTypes
    fun createShader(shaderName: String, shaderType: Int): UInt{
        val compiledShader = compileShaderFromFile(shaderName, shaderType)
        glAttachShader(program, compiledShader)
        return compiledShader
    }
}

@ExperimentalUnsignedTypes
class StaticShader : ShaderProgram(){
    private val vertexShader = this.createShader("basic", GL_VERTEX_SHADER)
    private val fragmentShader = this.createShader("basic", GL_FRAGMENT_SHADER)

    override fun cleanup() {
        this.deleteShader(vertexShader)
        this.deleteShader(fragmentShader)
    }

    override fun bindAttributes() {
        this.bindAttribute(0u, "position")
    }

}





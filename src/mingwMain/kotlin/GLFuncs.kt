import glfw.*
import glew.*
import kotlinx.cinterop.*
import sample.checkError

/**
 * Converts the [buffer] array argument into a C array before then passing it into [glGenBuffers]
 *
 * @param size - The size of the buffer
 * @param buffer - An array
 */
@ExperimentalUnsignedTypes
fun NativePlacement.glGenBuffers(size: Int): CArrayPointer<UIntVar>{
    val buf = allocArray<UIntVar>(UInt.SIZE_BYTES)
    glGenBuffers?.invoke(size, buf)
    return buf
}

fun NativePlacement.glBindBuffer(bufferType: Int, buffer: CArrayPointer<UIntVar>?){
    glBindBuffer?.invoke(bufferType.convert(), buffer?.pointed?.value ?: 0.convert())
}

@ExperimentalUnsignedTypes
fun NativePlacement.glUseProgram(shader: UInt){
    glUseProgram?.invoke(shader) ?: throw Exception("Could not use shader program $shader")
}

@ExperimentalUnsignedTypes
fun glAttachShader(program: UInt, shader: UInt){
    glAttachShader?.invoke(program, shader) ?: throw Exception("Could not attach vertex shader")
}

@ExperimentalUnsignedTypes
fun glDeleteShader(shaderId: UInt){
    glDeleteShader?.invoke(shaderId) ?: throw Exception("Could not delete vertex shader")

}

/**
 * Returns true if glewInit came back GLEW_OK
 */
fun glewInit(): Boolean{
    return (glew.glewInit().convert<Int>() == GLEW_OK)
}

fun NativePlacement.glBufferData(bufferType: Int, bufferData: Array<Float>, drawType: Int){
    val buf = allocArray<FloatVar>(bufferData.size){ index ->
        this.value = bufferData[index]
    }
    glBufferData?.invoke(bufferType.convert(), sizeOf<FloatVar>() * bufferData.size, buf, drawType.convert())
}

@ExperimentalUnsignedTypes
fun NativePlacement.glBufferData(bufferType: Int, bufferData: Array<UInt>, drawType: Int){
    val buf = allocArray<UIntVar>(bufferData.size){ index ->
        this.value = bufferData[index]
    }
    glBufferData?.invoke(bufferType.convert(), sizeOf<UIntVar>() * bufferData.size, buf, drawType.convert())
}

fun NativePlacement.glEnableVertexAttribArray(enable: Boolean){
    val e = if(enable) 0 else 1
    glEnableVertexAttribArray?.invoke(e.convert())
}

fun NativePlacement.glVertexAttribPointer(index: Int, vertexSize: Int, type: Int, normalized: Boolean){
    val stride = when(type){
        GL_FLOAT -> sizeOf<GLfloatVar>() * vertexSize
        GL_INT -> sizeOf<GLintVar>() * vertexSize
        else -> 0
    }
    glVertexAttribPointer?.invoke(index.convert(), vertexSize.convert(), type.convert(), if(normalized) GL_TRUE.convert() else GL_FALSE.convert(), stride.convert(), null)
}
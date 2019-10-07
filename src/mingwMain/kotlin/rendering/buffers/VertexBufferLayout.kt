package rendering.buffers

import VertexBuffer
import VertexBufferFloat
import VertexBufferUInt
import glBindVertexArray
import glEnableVertexAttribArray
import glGenBuffers
import glGenVertexArrays
import glVertexAttribPointer
import glew.*
import kotlinx.cinterop.*

@ExperimentalUnsignedTypes
class VertexBufferLayout{
    val elements = arrayListOf<VertexBufferElement>()
    var stride: UInt = 0u

    fun pushUInt(count: UInt){
        this.elements.add(VertexBufferElement(GL_UNSIGNED_INT.convert(), count.convert(), false))
        this.stride += count * getSizeOfType(GL_UNSIGNED_INT.convert())
    }

    fun pushFloat(count: UInt){
        this.elements.add(VertexBufferElement(GL_FLOAT.convert(), count.convert(), false))
        this.stride += count * getSizeOfType(GL_FLOAT.convert())
    }
}

@ExperimentalUnsignedTypes
data class VertexBufferElement(val type: UInt, val count: UInt, val normalized: Boolean)

@ExperimentalUnsignedTypes
fun getSizeOfType(type: UInt) = when(type){
    GL_FLOAT.convert<UInt>() -> 4u
    GL_UNSIGNED_INT.convert<UInt>() -> 4u
    GL_UNSIGNED_BYTE.convert<UInt>() -> 1u
    else -> 0u
}

@ExperimentalUnsignedTypes
class VertexArray<T> {
    private val rendererID = nativeHeap.glGenVertexArrays(1)

    fun addBuffer(vertexBuffer: VertexBuffer<T>, bufferLayout: VertexBufferLayout){
        vertexBuffer.bind()
        bufferLayout.elements.withIndex().forEach {(i, element) ->
            nativeHeap.glEnableVertexAttribArray(true)
            nativeHeap.glVertexAttribPointer(i, element.count.convert(), element.type.convert(), bufferLayout.stride, element.normalized)
        }

    }

    fun bind(){
        nativeHeap.glBindVertexArray(this.rendererID)
    }

    fun unbind(){
        nativeHeap.glBindVertexArray(null)
    }
}

@ExperimentalUnsignedTypes
fun vertexArrayFloat(data: Array<Float>, initializer: (VertexArray<Float>, VertexBufferFloat, VertexBufferLayout)->Unit): VertexArray<Float>{
    val va = VertexArray<Float>()
    val vb = VertexBufferFloat(data)
    val layout = VertexBufferLayout()
    initializer(va, vb, layout)
    return va
}

@ExperimentalUnsignedTypes
fun vertexArrayUInt(data: Array<UInt>, initializer: (VertexArray<UInt>, VertexBufferUInt, VertexBufferLayout)->Unit): VertexArray<UInt>{
    val va = VertexArray<UInt>()
    val vb = VertexBufferUInt(data)
    val layout = VertexBufferLayout()
    initializer(va, vb, layout)
    return va
}
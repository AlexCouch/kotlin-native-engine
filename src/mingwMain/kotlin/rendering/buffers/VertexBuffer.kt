import glew.GL_ARRAY_BUFFER
import glew.GL_STATIC_DRAW
import kotlinx.cinterop.*
import rendering.buffers.AbstractBuffer

@ExperimentalUnsignedTypes
sealed class VertexBuffer<T>(override val data: Array<T>) : AbstractBuffer<T>(data){

    override fun bind(){
        nativeHeap.glBindBuffer(GL_ARRAY_BUFFER, this.buffer)
        this.bufferData()
    }
    override fun unbind(){
        nativeHeap.glBindBuffer(GL_ARRAY_BUFFER, null)
    }
}

@ExperimentalUnsignedTypes
class VertexBufferUInt(data: Array<UInt>) : VertexBuffer<UInt>(data){
    override fun bufferData() {
        nativeHeap.glBufferData(GL_ARRAY_BUFFER, this.data, GL_STATIC_DRAW)
    }
}

@ExperimentalUnsignedTypes
class VertexBufferFloat(data: Array<Float>) : VertexBuffer<Float>(data){
    override fun bufferData() {
        nativeHeap.glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW)
    }
}
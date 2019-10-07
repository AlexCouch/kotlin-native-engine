package rendering

import glBindBuffer
import glBufferData
import glew.GL_ELEMENT_ARRAY_BUFFER
import glew.GL_STATIC_DRAW
import kotlinx.cinterop.nativeHeap
import rendering.buffers.AbstractBuffer

@ExperimentalUnsignedTypes
class IndexBuffer(override val data: Array<UInt>) : AbstractBuffer<UInt>(data){
    val count = data.size

    override fun bind() {
        nativeHeap.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.buffer)
        this.bufferData()
    }

    override fun unbind() {
        nativeHeap.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, null)
    }

    override fun bufferData() {
        nativeHeap.glBufferData(GL_ELEMENT_ARRAY_BUFFER, this.data, GL_STATIC_DRAW)
    }

}
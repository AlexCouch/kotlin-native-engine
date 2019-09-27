import glGenBuffers
import glew.*
import kotlinx.cinterop.*

class Renderer{
    fun prepare(){
        glClearColor(1f, 0f, 0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)
    }

    fun render(model: RawModel){
        glBindVertexArray?.invoke(model.vao.pointed.value)
        glEnableVertexAttribArray?.invoke(0.convert())
        glDrawElements(GL_TRIANGLES, model.vertexCount, GL_UNSIGNED_INT, null)
        glDisableVertexAttribArray?.invoke(0.convert())
        glBindVertexArray?.invoke(0.convert())
    }
}

@ExperimentalUnsignedTypes
class ModelLoader{
    private var vaos = hashMapOf<Int, CArrayPointer<UIntVar>>()
    private var vbos = hashMapOf<Int, CArrayPointer<UIntVar>>()

    fun loadToVAO(positions: Array<Float>, indices: Array<UInt>): RawModel{
        val vao = createVAO(positions.size)
        val vbo = bindIndicesBuffer(indices)
        storeDataInAttributeList(0, positions)
        unbindVAO()
        return RawModel(vao, vbo, indices.size)
    }

    fun cleanup(){
        vaos.forEach { (size, ptr) ->
            glDeleteVertexArrays?.invoke(size, ptr)
        }
        vbos.forEach { (size, ptr) ->
            glDeleteVertexArrays?.invoke(size, ptr)
        }
    }

    private fun bindIndicesBuffer(indices: Array<UInt>): CArrayPointer<UIntVar>{
        val buffer = nativeHeap.glGenBuffers(indices.size)
        vbos[indices.size] = buffer
        nativeHeap.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffer)
        nativeHeap.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
        return buffer
    }

    private fun storeDataInAttributeList(attributeNumber: Int, data: Array<Float>){
        val buffers = nativeHeap.glGenBuffers(data.size)
        vaos[data.size] = buffers
        nativeHeap.glBindBuffer(GL_ARRAY_BUFFER, buffers)
        nativeHeap.glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW)
        nativeHeap.glVertexAttribPointer(attributeNumber, 3, GL_FLOAT, false)
        nativeHeap.glBindBuffer(GL_ARRAY_BUFFER, null)
    }

    private fun unbindVAO(){
        glBindVertexArray?.invoke(0u.convert())
    }

    private fun createVAO(size: Int): CArrayPointer<UIntVar>{
        val vertexArrays = nativeHeap.allocArray<UIntVar>(sizeOf<UIntVar>() * size)
        glGenVertexArrays?.invoke(16, vertexArrays) ?: throw Exception("Cannot gen vertex arrays")
        glBindVertexArray?.invoke(vertexArrays.pointed.value)
        return vertexArrays
    }
}
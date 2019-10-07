import glew.*
import kotlinx.cinterop.*
import rendering.Model
import rendering.Texture

@ExperimentalUnsignedTypes
class Renderer{
    private var models = hashMapOf<String, Model>()

    fun prepare(){
        glClearColor(1f, 0f, 0f, 1f)
        glClear(GL_COLOR_BUFFER_BIT)
    }

    fun registerModel(name: String, model: Model){
        this.models[name] = model
    }

    fun render(){
        this.models.forEach { (name, model) ->
            println("Rendering model: $name")
            val va = model.vertexArray
            val ib = model.indexBuffer
            val shader = model.shader

//            println("Starting shader")
            shader.startProgram()
//            println("Binding vertex array")
            va.bind()
//            println("Binding index buffer")
            ib.bind()
            println(ib.count)
//            println("Drawing elements")
            glDrawElements(GL_TRIANGLES, ib.count, GL_UNSIGNED_INT, null)

//            println("Stopping shader")
//            shader.stopProgram()
        }
    }

    fun stop(){
        this.models.forEach { (name, model) ->
            println("Cleaning up mode: $name")
            model.shader.cleanup()
        }
    }
}

@ExperimentalUnsignedTypes
class ModelLoader{
    private var vaos = hashMapOf<Int, CArrayPointer<UIntVar>>()
    private var vbos = hashMapOf<Int, CArrayPointer<UIntVar>>()
    private var textures = arrayListOf<Texture>()

//    fun loadToVAO(positions: Array<Float>, indices: Array<UInt>): VertexArray{
//        val vao = createVAO(positions.size)
//        bindIndicesBuffer(indices)
//        storeDataInAttributeList(0, positions)
//        unbindVAO()
//        return RawModel(vao, indices.size)
//    }

    fun cleanup(){
        vaos.forEach { (size, ptr) ->
            glDeleteVertexArrays?.invoke(size, ptr)
        }
        vbos.forEach { (size, ptr) ->
            glDeleteVertexArrays?.invoke(size, ptr)
        }
        textures.forEach {
            it.delete()
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
//        nativeHeap.glVertexAttribPointer(attributeNumber, 3, GL_FLOAT, false)
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

    fun loadTexture(fileName: String): Texture{
        val texture = Texture(fileName)
        this.textures.add(texture)
        return texture
    }
}
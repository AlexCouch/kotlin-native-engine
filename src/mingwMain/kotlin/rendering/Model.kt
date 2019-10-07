package rendering

import BasicShader
import ShaderProgram
import rendering.buffers.VertexArray
import rendering.buffers.vertexArrayFloat

@ExperimentalUnsignedTypes
abstract class Model(val shader: ShaderProgram){
    protected abstract val positions: Array<Float>
    protected abstract val indicies: Array<UInt>

    val vertexArray: VertexArray<Float>
        get() = vertexArrayFloat(positions) { array, buffer, layout ->
                    println("Creating vertex array of floats")
                    layout.pushFloat(2)
                    array.addBuffer(buffer, layout)
                }

    val indexBuffer: IndexBuffer
        get() = IndexBuffer(indicies)

}

@ExperimentalUnsignedTypes
class BasicModel : Model(BasicShader()){
    override val positions: Array<Float> = arrayOf(
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f,  0.5f,
            -0.5f, 0.5f
        )
    override val indicies: Array<UInt>
        get() = arrayOf(
            0u, 1u, 2u,
            2u, 3u, 0u
        )

}
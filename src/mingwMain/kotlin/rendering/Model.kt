package rendering

import BasicShader
import ColoredShader
import ShaderProgram
import TexturedShader
import VertexBufferFloat
import glew.GL_TRIANGLES
import glew.GL_UNSIGNED_INT
import glew.glDrawElements
import rendering.buffers.IndexBuffer
import rendering.buffers.VertexArray
import rendering.buffers.vertexArrayFloat
import sample.checkError

@ExperimentalUnsignedTypes
abstract class Model(val shader: ShaderProgram){
    protected abstract val positions: Array<Float>
    protected abstract val indicies: Array<UInt>

    abstract val vertexArray: VertexArray<Float>
    abstract val indexBuffer: IndexBuffer

    open fun preRender(){
        //OPTIONAL
    }

    fun render(){
        shader.startProgram()
        this.draw()
        this.vertexArray.bind()
        this.indexBuffer.bind()
        glDrawElements(GL_TRIANGLES, this.indexBuffer.count, GL_UNSIGNED_INT, null)
    }
    abstract fun draw()
}

@ExperimentalUnsignedTypes
abstract class BasicModel(_shader: ShaderProgram) : Model(_shader){
    override val vertexArray: VertexArray<Float>
        get() = vertexArrayFloat(positions) { array, buffer, layout ->
            array.bind()
            layout.pushFloat(2u)
            array.addBuffer(buffer, layout)
            array.unbind()
        }

    override val indexBuffer: IndexBuffer
        get() = IndexBuffer(indicies)

    override val positions: Array<Float>
        get() = arrayOf(
            -0.5f, -0.5f,
             0.5f, -0.5f,
             0.5f,  0.5f,
            -0.5f,  0.5f
        )
    override val indicies: Array<UInt>
        get() = arrayOf(
            0u, 1u, 2u,
            3u, 2u, 0u
        )
}

@ExperimentalUnsignedTypes
class ColoredModel(private var r: Float, private var g: Float, private var b: Float, private var a: Float): BasicModel(_shader=ColoredShader){

    private var reverse = false

    override fun draw(){
        if(this.r + 0.05f > 1.0f) this.reverse = true
        if(this.r - 0.05f < 0.0f) this.reverse = false

        this.r = if(this.reverse) this.r - 0.05f else this.r + 0.05f

        this.shader.setUniform4f("u_Color", r, g, b, a)
    }
}

@ExperimentalUnsignedTypes
class TexturedModel(texturePath: String) : Model(TexturedShader){
    private val texture = Texture(texturePath)

    override val vertexArray: VertexArray<Float>
        get() = vertexArrayFloat(positions) { array, buffer, layout ->
            array.bind()
            layout.pushFloat(2u)
            layout.pushFloat(2u)
            array.addBuffer(buffer, layout)
            array.unbind()
        }

    override val indexBuffer: IndexBuffer
        get() = IndexBuffer(indicies)

    override val positions: Array<Float>
        get() = arrayOf(
            -0.5f, -0.5f, 0.0f, 0.0f,
             0.5f, -0.5f, 1.0f, 0.0f,
             0.5f,  0.5f, 1.0f, 1.0f,
            -0.5f,  0.5f, 0.0f, 1.0f
        )

    override val indicies: Array<UInt>
        get() = arrayOf(
            0u, 1u, 2u,
            3u, 2u, 0u
        )

    override fun draw() {
        checkError()
        this.texture.bind(0u)
        this.shader.setUniformi("u_Texture", 0)
    }

}
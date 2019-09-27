import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.UIntVar

class RawModel(val vao: CArrayPointer<UIntVar>, val vbo: CArrayPointer<UIntVar>, val vertexCount: Int)

class TexturedModel{
    private val texture_id = -1
}
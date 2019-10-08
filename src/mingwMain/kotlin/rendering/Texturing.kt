package rendering

import glew.*
import kotlinx.cinterop.*
import stb_image.stbi_image_free
import stb_image.stbi_load
import stb_image.stbi_set_flip_vertically_on_load

@ExperimentalUnsignedTypes
@Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
class Texture(val texturePath: String){
    private var width = 0
    private var height = 0
    private var bpp = 0
    private var rendererID = 0u
    private var localBuffer: CArrayPointer<UByteVar>? = null

    init{
        stbi_set_flip_vertically_on_load(1)
        localBuffer = stbi_load(this.texturePath, cValuesOf(this.width), cValuesOf(this.height), cValuesOf(bpp), 4) ?: throw Exception("Could not find texture at path $texturePath")

        glGenTextures(1, cValuesOf(this.rendererID))
        glBindTexture(GL_TEXTURE_2D, this.rendererID)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, this.localBuffer)
        glBindTexture(GL_TEXTURE_2D, 0)

        if(this.localBuffer != null){
            stbi_image_free(this.localBuffer)
        }
    }

    fun delete(){
        glDeleteTextures(GL_TEXTURE_2D, cValuesOf(this.rendererID))
    }

    fun bind(slot: UInt){
        glActiveTexture?.invoke(GL_TEXTURE0.convert<UInt>() + slot)
        glBindTexture(GL_TEXTURE_2D, this.rendererID)
    }
}
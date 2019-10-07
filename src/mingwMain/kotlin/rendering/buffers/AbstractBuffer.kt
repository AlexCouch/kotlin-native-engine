package rendering.buffers

import glGenBuffers
import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.nativeHeap

@ExperimentalUnsignedTypes
abstract class AbstractBuffer<T>(protected open val data: Array<T>){
    protected var buffer: CArrayPointer<UIntVar> = nativeHeap.glGenBuffers(1)

    abstract fun bind()
    abstract fun unbind()
    abstract fun bufferData()
}
package sample

import Renderer
import BasicShader
import glewInit
import glfw.*
import kotlinx.cinterop.*
import rendering.BasicModel

fun checkError(){
    memScoped {
        val description = allocArray<ByteVar>(sizeOf<ByteVar>())
        val error = glfwGetError(description.reinterpret())
        if(error != GLFW_NO_ERROR){
            println(description.toKString())
        }
    }
}

@ExperimentalUnsignedTypes
fun main() {
    if(glfwInit() == GLFW_FALSE){
        println("Could not initialize glfw.")
        glfwTerminate()
    }

    val window = glfwCreateWindow(640, 480, "Kotlin Native OpenGL", null, null)
    if(window == null){
        glfwTerminate()
    }
    glfwMakeContextCurrent(window)

    if(!glewInit()){
        checkError()
        glfwTerminate()
        return
    }
    println("Glew Init Successful!")

    val renderer = Renderer()
    println("Renderer created!")
    val basicModel = BasicModel()
    println("Basic model created")
    renderer.registerModel("basic_model", basicModel)
    println("Basic model registered!")

    while(glfwWindowShouldClose(window) == GLFW_FALSE){
        renderer.prepare()
        renderer.render()
        glfwSwapBuffers(window)
        checkError()
        glfwPollEvents()
    }
    renderer.stop()
    glfwTerminate()
}
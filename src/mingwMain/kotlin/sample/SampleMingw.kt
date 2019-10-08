package sample

import Renderer
import BasicShader
import glewInit
import glfw.*
import kotlinx.cinterop.*
import rendering.BasicModel
import rendering.ColoredModel
import rendering.TexturedModel

fun checkError(){
    val description = nativeHeap.allocArray<ByteVar>(sizeOf<ByteVar>())
    val error = glfwGetError(description.reinterpret())
    if(error != GLFW_NO_ERROR){
        println(description.toKString())
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
//    println("Glew Init Successful!")

    val renderer = Renderer()
    val texturedModel = TexturedModel("src/mingwMain/resources/textures/test.png")
    renderer.registerModel("textured_model", texturedModel)
//    val coloredModel = ColoredModel(1.0f, 0.0f, 1.0f, 1.0f)
//    renderer.registerModel("colored_model", coloredModel)

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
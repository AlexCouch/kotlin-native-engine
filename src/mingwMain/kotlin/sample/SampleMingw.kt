package sample

import ModelLoader
import Renderer
import ShaderProgram
import StaticShader
import glBindBuffer
import glBufferData
import glEnableVertexAttribArray
import glGenBuffers
import glVertexAttribPointer
import glew.*
import glewInit
import glfw.*
import kotlinx.cinterop.*

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

    val loader = ModelLoader()
    val renderer = Renderer()
    val shader = StaticShader()

    val vertices = arrayOf(
        -0.5f,  0.5f, 0f,
        -0.5f, -0.5f, 0f,
         0.5f, -0.5f, 0f,
         0.5f, 0.5f, 0f
    )

    val indices = arrayOf(
        0u, 1u, 3u,
        3u, 1u, 2u
    )

    val rawModel = loader.loadToVAO(vertices, indices)

    while(glfwWindowShouldClose(window) == GLFW_FALSE){
        renderer.prepare()
        shader.startProgram()
        renderer.render(rawModel)
        shader.stopProgram()
        glfwSwapBuffers(window)
        checkError()
        glfwPollEvents()
    }

    shader.cleanup()
    loader.cleanup()
    glfwTerminate()
}
package sample

import ShaderProgram
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

    memScoped {
        val buffers = glGenBuffers(1)
        glBindBuffer(GL_ARRAY_BUFFER, buffers)
        val positions = arrayOf(-0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f, 0.5f)
        glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW)
        glEnableVertexAttribArray(true)
        glVertexAttribPointer(0, 2, GL_FLOAT, false)

        val indexBuffers = glGenBuffers(1)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffers)
        val indices = arrayOf(
            0u, 1u, 2u,
            2u, 3u, 0u
        )
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        val shader = ShaderProgram()
        shader.createShader("basic", GL_FRAGMENT_SHADER)
        shader.createShader("basic", GL_VERTEX_SHADER)
        shader.startProgram()
    }


    while(glfwWindowShouldClose(window) == GLFW_FALSE){
        checkError()
        glClear(GL_COLOR_BUFFER_BIT)
        checkError()
        glDrawElements(GL_TRIANGLES.convert(), 6.convert(), GL_UNSIGNED_INT.convert(), null)
        checkError()
        glfwSwapBuffers(window)
        checkError()
        glfwPollEvents()
    }

    glfwTerminate()
}
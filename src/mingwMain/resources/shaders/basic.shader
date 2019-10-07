#shader vertex
#version 410 core

layout(location = 0) in vec4 position;

//out vec4 v_Color;

void main(){
    gl_Position = position;
//    v_Color = position.rgba;
}

#shader fragment
#version 410 core

//in vec4 v_Color;

layout(location = 0) out vec4 color;

void main(){
    color = vec4(1.0, 0.0, 1.0, 1.0);
}


#version 330 core

layout(location = 0) in vec4 position;

out vec4 vert_color;

void main(){
    gl_Position = position;
    vert_color = vec4(position.x+0.5f, position.y*2, position.z+0.5f, 1.0f);
}
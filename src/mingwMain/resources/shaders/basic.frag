#version 330 core

in vec4 vert_color;
layout(location = 0) out vec4 color;

void main(){
    color = vec4(vert_color.xyz, 1.0);
}
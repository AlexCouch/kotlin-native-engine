#shader vertex
#version 410 core

layout(location = 0) in vec4 position;
layout(location = 1) in vec2 texCoord;

out vec2 v_TexCoord;

void main(){
    gl_Position = position;
    v_TexCoord = texCoord;
}

#shader fragment
#version 410 core

//in vec4 v_Color;

layout(location = 0) out vec4 color;

in vec2 v_TexCoord;

uniform sampler2D u_Texture;

void main(){
    color = texture(u_Texture, v_TexCoord);
}
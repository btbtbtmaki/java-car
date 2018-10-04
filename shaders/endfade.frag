#version 330 core

layout (location = 0) out vec4 color;

uniform float time;

void main()
{
	
	color = vec4(1.0, 0, 0,  time);
}
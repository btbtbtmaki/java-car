#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
	vec2 tc;
	vec3 position;
} fs_in;

uniform vec2 player;
uniform sampler2D tex;
uniform int top;

void main()
{


	vec2 myTc = vec2(fs_in.tc.x, fs_in.tc.y);
    if (top == 1) {
        myTc.y = 1 - myTc.y;
    }
		
	color = texture(tex, fs_in.tc);
	if (color.w < 1.0)
		discard;
	color *= 2.0 / (length(player - fs_in.position.xy) + 1.5) + 0.5;
	color.w = 1.0;
}
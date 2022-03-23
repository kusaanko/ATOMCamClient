#version 150 core

in vec3 position;
in vec2 texPosition;

out vec2 TexCoords;

uniform mat4 projection;

void main()
{
    gl_Position = /*projection * */vec4(position, 1.0);
    TexCoords = texPosition;
}

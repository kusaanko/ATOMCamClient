#version 150 core

in vec2 TexCoords;
out vec4 color;

uniform sampler2D image;

void main()
{
    color = texture(image, TexCoords);
}

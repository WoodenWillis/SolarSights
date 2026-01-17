#version 320 es

uniform mat4 u_combinedMatrix;
uniform mat4 u_worldMatrix;
uniform mat4 u_ringMatrix;

in vec3 a_position;
in vec2 a_texCoord0;

out vec2 uv;
out vec3 pos;

void main() {

    uv = a_texCoord0;

    pos = a_position.xyx;
    gl_Position = u_combinedMatrix * u_worldMatrix * u_ringMatrix * vec4(a_position.x, a_position.y, a_position.z, 1.);
}
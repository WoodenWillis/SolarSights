#version 320 es

uniform mat4 u_combinedMatrix;
uniform mat4 u_worldMatrix;

in vec3 a_position;
in float a_weight;

out float v_weight;

void main() {
    v_weight = a_weight;
    gl_Position = u_combinedMatrix * u_worldMatrix* vec4(a_position.x, a_position.y, a_position.z, 1.);
}
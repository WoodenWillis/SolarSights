#version 320 es

uniform mat4 u_projectionMatrix;
uniform mat4 u_worldMatrix;
uniform float u_main_opacity;
uniform vec4 u_color;

in float a_size;
in vec4 a_position;
in float a_opacity;

out float v_opacity;
out vec4 v_color;

void main() {
    v_opacity = a_opacity * u_main_opacity;
    v_color = u_color;
    gl_Position = u_projectionMatrix * u_worldMatrix * a_position;
    gl_PointSize = a_size / (sqrt(gl_Position.w) * 5.0f);
}
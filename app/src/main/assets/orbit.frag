#version 320 es

precision highp float;

uniform sampler2D u_texture;
uniform float u_opacity;
uniform float u_radius;
uniform vec3 u_color;
uniform float u_aod;

in float v_weight;
out vec4 fragColor;

void main() {
    float a = 0.25 + u_radius * v_weight;
    float b = 1.;
    float alpha = step(0., a - (b * floor(a/b))) * u_opacity;
    alpha *=  1.0 - u_aod;
    fragColor = vec4(u_color.r, u_color.g, u_color.b, alpha);
}
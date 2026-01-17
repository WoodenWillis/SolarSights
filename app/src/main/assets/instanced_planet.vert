#version 320 es
layout(location = 0) in vec2 a_Position;
layout(location = 1) in vec4 a_OrbitParams;
layout(location = 2) in vec4 a_Color;

uniform float u_Time;
uniform float u_Aspect;
uniform float u_Offset; // parallax

out vec4 v_Color;
out vec2 v_UV;

void main() {
    v_Color = a_Color;
    v_UV = a_Position;

    float theta = (u_Time * a_OrbitParams.y) + a_OrbitParams.w;
    float orbitX = cos(theta) * a_OrbitParams.x;
    float orbitY = sin(theta) * a_OrbitParams.x;

    vec2 planetSize = a_Position * a_OrbitParams.z;
    planetSize.x /= u_Aspect;

    // Apply parallax shift to the final X position
    gl_Position = vec4(orbitX + planetSize.x + u_Offset, orbitY + planetSize.y, 0.0, 1.0);
}
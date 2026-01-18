#version 320 es
// assets/orbit_ring.vert
layout(location = 0) in vec4 a_Position;    // Standard Quad
layout(location = 1) in vec4 a_OrbitParams; // We only need .x (Radius)

uniform float u_Aspect;
uniform vec2 u_Center;

out float v_Radius;
out vec2 v_UV;

void main() {
    // Pass radius to fragment shader
    v_Radius = a_OrbitParams.x;

    // Pass UVs (centered at 0,0)
    v_UV = a_Position.xy;
    v_UV.x *= u_Aspect; // Correct aspect so rings are circles, not ovals

    // Draw a quad that covers the specific orbit area
    // We scale the quad to be slightly larger than the radius to save pixels
    float size = v_Radius + 0.05;
    gl_Position = vec4(a_Position.xy * size, 0.0, 1.0);

    // Correct UVs again for the fragment math
    v_UV = a_Position.xy * size;
    v_UV.x *= u_Aspect;
    float tilt = radians(35.0);
    float tiltSin = sin(tilt);

    // When drawing the quad:
    gl_Position = vec4(a_Position.x * size, a_Position.y * size * tiltSin, 0.0, 1.0);

    // And for v_UV used in fragment math:
    v_UV = vec2(a_Position.x * size, a_Position.y * size * tiltSin);
    v_UV.x *= u_Aspect;

}

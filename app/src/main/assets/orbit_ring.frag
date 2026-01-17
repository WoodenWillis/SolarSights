#version 320 es
// assets/orbit_ring.frag
precision mediump float;

in float v_Radius;
in vec2 v_UV;
out vec4 fragColor;

void main() {
    float dist = length(v_UV);

    // Calculate distance from the ideal orbit radius
    float diff = abs(dist - v_Radius);

    // Draw a thin line (0.005 width) with anti-aliasing
    float alpha = 1.0 - smoothstep(0.0, 0.005, diff);

    if (alpha <= 0.0) discard;

    // Faint grey color
    fragColor = vec4(1.0, 1.0, 1.0, 0.15 * alpha);
}
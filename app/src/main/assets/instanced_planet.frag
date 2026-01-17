#version 320 es
precision highp float;

uniform vec3 u_light; // Must match vertex or be unique

in vec2 uv;
in vec3 v_color_dark;
in vec3 v_color_light;
in vec3 v_normal;

out vec4 fragColor;

float circle(in vec2 _st, in float _radius){
    vec2 dist = _st - vec2(0.5);
    return 1.0 - smoothstep(_radius - (_radius * 0.01), _radius + (_radius * 0.01), dot(dist, dist) * 4.0);
}

void main() {
    if (circle(uv, 0.9) == 0.0) discard;

    // Fake 3D lighting on a flat circle
    vec2 p = uv * 2.0 - 1.0;
    float z = sqrt(max(0.0, 1.0 - p.x*p.x - p.y*p.y));
    vec3 N = normalize(vec3(p.x, p.y, z));
    vec3 L = normalize(u_light);

    float lightDot = max(dot(N, L), 0.0);
    vec3 finalColor = mix(v_color_dark, v_color_light, lightDot);

    fragColor = vec4(finalColor, 1.0);
}
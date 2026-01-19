#version 320 es
precision highp float;

uniform float u_Aspect;

in vec2 v_Local;
in vec4 v_Color;
in vec2 v_ToSun;

out vec4 fragColor;

void main() {
    // Aspect-correct local coords so the disc is circular in screen space
    vec2 p = vec2(v_Local.x * u_Aspect, v_Local.y);
    float r2 = dot(p, p);
    if (r2 > 1.0) discard;

    float r = sqrt(r2);

    // Analytic AA edge
    float aa = fwidth(r);
    float alpha = 1.0 - smoothstep(1.0 - aa, 1.0 + aa, r);

    // Sphere normal from disc coords
    float z = sqrt(max(0.0, 1.0 - r2));
    vec3 n = normalize(vec3(p, z));

    // Light direction (slight forward bias for a soft terminator)
    vec3 l = normalize(vec3(v_ToSun, 0.35));

    float ndotl = max(dot(n, l), 0.0);

    vec3 base = v_Color.rgb;
    vec3 lit  = mix(base, vec3(1.0), 0.35);

    float diffuse = smoothstep(0.0, 1.0, ndotl);
    float rim = pow(1.0 - z, 2.0) * 0.18;

    vec3 color = mix(base, lit, diffuse) + rim;

    fragColor = vec4(color, v_Color.a * alpha);
}

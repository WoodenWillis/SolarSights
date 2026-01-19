#version 320 es
precision highp float;

// Matches SolarRenderer.kt attribute setup:
// location 0: quad position (vec2)
// location 1: orbit params (vec4) = (radius, speed, size, phase)
// location 2: color (vec4)
layout(location = 0) in vec2 a_Position;
layout(location = 1) in vec4 a_OrbitParams;
layout(location = 2) in vec4 a_Color;

uniform float u_Time;
uniform float u_Aspect;
uniform float u_Offset;
uniform vec2  u_Center;

out vec2 v_Local;
out vec4 v_Color;
out vec2 v_ToSun;

void main() {
    float radius = a_OrbitParams.x;
    float speed  = a_OrbitParams.y;   // radians/sec in our accelerated time scale
    float size   = a_OrbitParams.z;
    float phase  = a_OrbitParams.w;

    float ang = phase + u_Time * speed;

    vec2 orbit = vec2(cos(ang), sin(ang)) * radius;

    // Same tilt feel as your orbit rings
    float tilt = radians(35.0);
    float tiltSin = sin(tilt);
    vec2 orbitView = vec2(orbit.x, orbit.y * tiltSin);

    vec2 center = u_Center + vec2(u_Offset, 0.0);
    vec2 planetCenter = center + orbitView;

    vec2 quad = a_Position * size;

    gl_Position = vec4(planetCenter + quad, 0.0, 1.0);

    v_Local = a_Position;
    v_Color = a_Color;

    // Light comes from the "sun" at orbit center, so direction planet->sun is -orbit
    vec2 toSun = -orbitView;
    float len2 = dot(toSun, toSun);
    v_ToSun = (len2 > 1e-6) ? normalize(toSun) : vec2(0.0, 1.0);
}

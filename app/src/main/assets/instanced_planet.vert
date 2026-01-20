#version 320 es
precision highp float;

in vec2 a_Position;      // quad: (-1,-1) .. (1,1)
in vec4 a_OrbitParams;   // x=radiusWorld, y=speed, z=sizeWorld, w=phase
in vec4 a_Color;

uniform float u_Time;
uniform mat4  u_ViewProj;
uniform vec3  u_CamRight;   // world-space
uniform vec3  u_CamUp;      // world-space
uniform float u_Tilt;       // radians (global orbit tilt)

out vec2 v_Local;
out vec4 v_Color;
out vec3 v_ToSun;

void main() {
    float radius = a_OrbitParams.x;
    float speed  = a_OrbitParams.y;
    float size   = a_OrbitParams.z;
    float phase  = a_OrbitParams.w;

    float ang = phase + u_Time * speed;

    // Orbit in world space (XZ plane)
    vec3 p = vec3(cos(ang) * radius, 0.0, sin(ang) * radius);

    // Tilt the orbit plane (gives the classic “ellipse” when projected)
    float c = cos(u_Tilt);
    float s = sin(u_Tilt);
    p = vec3(p.x, p.y * c - p.z * s, p.y * s + p.z * c);

    // Billboard quad in world space
    vec3 billboard = u_CamRight * (a_Position.x * size)
                   + u_CamUp    * (a_Position.y * size);

    vec3 worldPos = p + billboard;

    gl_Position = u_ViewProj * vec4(worldPos, 1.0);

    v_Local = a_Position;
    v_Color = a_Color;

    // Sun at origin
    v_ToSun = normalize(-p);
}

#version 320 es

uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;
uniform mat4 u_transform[24];

in vec3 a_position;
in vec2 a_texCoord0;

out vec2 uv;

void main() {
    uv = a_texCoord0;

    mat4 modelView = u_viewMatrix  *  u_transform[gl_InstanceID];
    float scale = modelView[0][0] + modelView[1][1] ;
    // First colunm.
    modelView[0][0] = scale;
    modelView[0][1] = 0.0;
    modelView[0][2] = 0.0;


    // Second colunm.
    modelView[1][0] = 0.0;
    modelView[1][1] = scale;
    modelView[1][2] = 0.0;


    // Thrid colunm.
    modelView[2][0] = 0.0;
    modelView[2][1] = 0.0;
    modelView[2][2] = scale;

    vec4 p =  u_projectionMatrix * modelView * vec4(a_position.xyz, 1.);
    gl_Position = p;
}
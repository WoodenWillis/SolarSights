#version 320 es
#define PLANETS 9

uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;
uniform mat4 u_normalMatrix;
uniform mat4 u_light;
uniform mat4 u_transform[PLANETS];

uniform vec4 u_color_dark[PLANETS];
uniform vec4 u_color_light[PLANETS];

in vec3 a_position;
in vec3 a_normal;
in vec2 a_texCoord0;

out vec3 v_normal;
out vec2 uv;
out vec2 v_inPlanet;
out vec3 v_color_dark;
out vec3 v_color_light;


void main() {

    // Even though we are using the transposed inverted ViewMatrix, set the 4th component to 0
    // since translation shouldn't modify a vector (it should only modify a point).


    vec4 planeNormal =  u_transform[gl_InstanceID] * vec4(0.1, 0.1, 0.1, 0.);
    v_normal = normalize(u_normalMatrix * vec4(1,0,0, 0.)).xyz;
    uv = (a_texCoord0 );


    mat4 modelView = u_viewMatrix  *  u_transform[gl_InstanceID];
    float scale = modelView[1][1];
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



    v_inPlanet = (( (p.xyz / p.w).xy + 1.0) / 2.0);

    v_color_light = u_color_light[gl_InstanceID].rgb;
    v_color_dark = u_color_dark[gl_InstanceID].rgb;





    //v_color_light= v_color_dark = planeNormal.xyz;
    gl_Position = p;

}
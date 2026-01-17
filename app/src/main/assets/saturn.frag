#version 320 es

precision highp float;

uniform float u_innerRadius;
uniform float u_outerRadius;
uniform float u_aod;
uniform float u_oval;
uniform vec4 u_color;



in vec2 uv;
in vec3 pos;

out vec4 fragColor;

void main() {
    vec2 center = vec2(0.5);
    float distanceFromCenter = length(uv - center);


    vec2 outerOval = vec2(u_outerRadius, u_outerRadius * u_oval);
    float outer1 =  ( uv.x - center.x ) / ( outerOval.x );
    float outer2 =  ( uv.y - center.y ) / ( outerOval.y );


    /*
    // Inner ellipse but i think it looks better with a perfect circle
    vec2 innerOval = vec2(u_innerRadius, u_innerRadius * u_oval);
    float inner1 =  ( uv.x - center.x ) / ( innerOval.x );
    float inner2 =  ( uv.y - center.y ) / ( innerOval.y );

    if ((outer1 * outer1) + (outer2 * outer2) > 1.0 || (inner1 * inner1) + (inner2 * inner2) < 1.0)
    */

    if ((outer1 * outer1) + (outer2 * outer2) > 1.0 || distanceFromCenter < u_innerRadius)
        discard;

    fragColor = vec4(u_color.rgb, 1.0 - u_aod);
}
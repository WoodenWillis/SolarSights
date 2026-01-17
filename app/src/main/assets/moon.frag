#version 320 es

precision highp float;

uniform vec4 u_color_light;
uniform float u_aod;

in vec2 uv;
out vec4 fragColor;



float circle(in vec2 _st, in float _radius){
    vec2 dist = _st-vec2(0.5);
    return 1.-smoothstep(_radius-(_radius*0.01),
    _radius+(_radius*0.01),
    dot(dist,dist)*4.0);
}


void main() {
    if (circle(uv,0.9) == 0.0) discard;

    fragColor = vec4(u_color_light.rgb, 1. - u_aod);
}
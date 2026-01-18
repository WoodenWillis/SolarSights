#version 320 es

precision highp float;

uniform sampler2D u_texture;
uniform sampler2D u_occlusionTexture;

uniform float u_opacity;
uniform float u_radius;
uniform vec3 u_light;
uniform float u_aod;

in vec3 v_normal;
in vec2 uv;
in vec2 v_inPlanet;
in vec3 v_color_dark;
in vec3 v_color_light;

out vec4 fragColor;



float circle(in vec2 _st, in float _radius){
    vec2 dist = _st-vec2(0.5);
    return 1.-smoothstep(_radius-(_radius*0.01),
    _radius+(_radius*0.01),
    dot(dist,dist)*4.0);
}

void main() {

    if (circle(uv,0.9) == 0.0) discard;

    vec3 N = normalize(v_normal);
    // Check http://www.lighthouse3d.com/tutorials/glsl-12-tutorial/directional-lights-ii/
    vec3 L = normalize(u_light);

    float lightDotProduct = dot(-L, N);
    vec3 tex = texture( u_texture, uv ).rgb;
    vec3 lambertian = clamp(lightDotProduct * tex, 0., 1.);

    vec3 color = mix(v_color_dark, v_color_light, lambertian);

    vec3 occlusion = 1.0 - texture(u_occlusionTexture, v_inPlanet).rgb;

    color *= occlusion;




    fragColor = vec4(color.rgb , 1.0 - u_aod);
    //fragColor = vec4(v_normal, 1.);
}
#version 320 es
precision highp float;

uniform vec2 u_resolution;

uniform float u_time;
uniform float u_aod;

uniform highp float u_noise_period;
uniform float u_noise_strength;
uniform float u_noise_scale;
uniform vec4 u_color_dark;
uniform vec4 u_color_clear;

in vec2 v_uv;

out vec4 fragColor;

/* Other Methods */
#define OCTAVES 4

//#define LACUNARITY 1.524
//#define GAIN .4

#define LACUNARITY 2.524
#define GAIN .3

#include <glsl/simplex.glsl>
#include <glsl/perlin_noise.glsl>
#include <glsl/fractal_brownian_motion.glsl>

float noise(in vec3 st) {
     return 0.5 * ((cnoise(st) * 0.5 / 0.72) + 0.5);
}

float pattern(in vec3 st) {
    vec3 q = vec3(0.);
    q.x = noise(st + 0.02 * u_time);
    q.y = noise(st + vec3(1.0));

    vec3 r = vec3(0.);
    r.x = noise(st + q + vec3(1.7, 9.2, 0.) + 0.03 * u_time);
    r.y = noise(st + q + vec3(8.3, 2.8, 0.) + 0.026 * u_time);

	return fbm(st + r + 0.05 * u_time);
}

void main()  {
    vec3 st = vec3(gl_FragCoord.xy / u_resolution.yy, 1.0);

    // Scale the space in order to see the function
    vec3 noisePos = vec3(st.xy, u_noise_period);
    noisePos.xy *= u_noise_scale;

    float f = pattern(noisePos);
    float fExp = .5 * f * f + .5 * f;
    float fNorm = fExp / (0.650 - 0.008);
//    fNorm /= 4.8;
    // Check MD noise
    // gl_FragColor = vec4(1. - step(0.008, f), step(1.820, f), 0.0, 1.0);
    fragColor = (1.0 - u_aod) * mix(u_color_dark, u_color_clear, fNorm);
//    fragColor = vec4(0, 0, 0, 1);
    fragColor = vec4(25./255., 25./255., 28./255., 1);
}
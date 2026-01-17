#version 320 es

precision highp float;

uniform sampler2D u_texture;

in float v_opacity;
in vec4 v_color;
out vec4 fragColor;

void main() {
  vec2 uv2 = (gl_PointCoord - 0.5) * 2.0;

  fragColor = v_color;
  fragColor.a = v_opacity * (1.-smoothstep(0.5, .7, uv2.x * uv2.x + uv2.y * uv2.y));
}
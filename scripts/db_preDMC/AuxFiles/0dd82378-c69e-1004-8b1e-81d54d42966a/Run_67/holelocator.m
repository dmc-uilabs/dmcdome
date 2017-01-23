function [edgetohole] = holelocator(holediameter, platedepth)
% plate depth: 30 ~ 50
% hole diameter: 10 ~ 20
edgetohole = holediameter * 0.55 + (50 - platedepth) / 30 * 4;

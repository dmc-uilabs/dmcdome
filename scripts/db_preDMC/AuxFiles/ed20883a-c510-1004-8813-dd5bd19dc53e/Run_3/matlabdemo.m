function [C D] = hp_grad_search(A, B)
C = A + B;
D = A * B;
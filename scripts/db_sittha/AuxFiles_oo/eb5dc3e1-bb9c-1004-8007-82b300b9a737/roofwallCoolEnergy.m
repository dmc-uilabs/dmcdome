function [qtot] = roofwallCoolEnergy(U, A, solairDegHrs)
load CTS wallCTS;

% conductive
qcon = U*A*solairDegHrs; % Btu
qtot = sum(qcon);
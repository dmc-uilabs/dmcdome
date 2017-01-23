function [qtot] = roofwallCoolEnergy(U, A, solairDegHrs)

% conductive
qcon = U*A*solairDegHrs; % Btu
qtot = sum(qcon);
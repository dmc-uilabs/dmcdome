function [AFR, Qinf, totQinf] = infiltrationHeatLoss(leakType, leakSize, numSto, shield, Tin, T975, wind)
load bldgHeatLoss effLeakage stackCoeff windCoeff;
for i=1:size(leakType)
    effLeak(i) = effLeakage(leakType(i));
end
effLeak = effLeak';

stackCo = stackCoeff(numSto);   % cfm^2/in^4-degF
windCo = windCoeff(shield,numSto);  % cfm^2/in^4-mph^2

totLeak = effLeak.*leakSize;   % in^2

AFR = totLeak*sqrt(stackCo*(Tin-T975) + windCo*wind^2); % cfm

Qinf = AFR*0.018*60*(Tin-T975); % (0.018 Btu/ft^3-degF * 60 min/h) --> Btu/h
totQinf = sum(Qinf);
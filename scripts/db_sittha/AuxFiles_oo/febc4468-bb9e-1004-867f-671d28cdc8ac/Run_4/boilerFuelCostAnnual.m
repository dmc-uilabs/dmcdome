%annualHeat = 2000000; % Btu
%annualDHW = 600000;   % Btu
%AFUE = 0.92;
%fuelHeatVal = 82000;   % NG: Btu/therm
%unitCost = 0.89;    % NG: $/therm
%%%%%%%%%%%%%%%%%

annualHeatInput = (annualHeat+annualDHW)/AFUE;
unitFuel = annualHeatInput/fuelHeatVal;
cost = unitFuel*unitCost;
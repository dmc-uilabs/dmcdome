function [qvtot, qvtotR] = ventilationHeatEnergy(AFR, heatDegHrs, Wi, cityID, weekNum, Tin, use)

%AFR = 250; % cfm
%heatDegHrs = [34.1	24.713	21.95	8.3967	6.1467	8.3433	10.833	27.577]; % degF-h
%Wi = 44; % gr/lb dry air    (70 dry-bulb temp & 40% rel humid)
%cityID = 725090;
%weekNum = 1;
% Tin = [-1 -1 -1 68 68 68 -1- -1];

%%%%%%%%%%%%%%%%%%%%%%%%

cityID = num2str(cityID);
query = ['load weather' cityID ' humidRatio'];
eval(query);
Wo = humidRatio(weekNum); % gr/lb dry air

% sensible heat loss
qvsen = 1.08*AFR*heatDegHrs;    % Btu
% 1.08 = 0.018 Btu/ft^3-degF * 60 min/h

% latent heat loss
qvlat = 4840*AFR*(Wi-Wo)/7000;  % Btu/h
% 4840 = 0.075 lb/ft^3 * 60 min/h * 1076 Btu/lb
venHr = 0;
for i = 1:8
    if Tin(i) ~= -1 % controlled period
        venHr = venHr + use(i*3-2)/100 + use(i*3-1)/100 + use(i*3)/100;
    end
end
qvlat_ = qvlat*venHr;

% 7000 grains of moisture in a lb
% heat capacity of air = 0.018 Btu/ft^3-degF
% density of air = 0.075 lb/ft^3
% approximate heat content of 50% RH vapor at 75 degF, less the heat
% content of water at 50 degF = 1076 Btu/lb

% total ventilation heat loss
qvtot = sum(qvsen+qvlat_);    % Btu

% if uncontrolled period is not neglected
qvtotR = sum(sum(use)/100*qvlat+qvsen);
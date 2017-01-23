function [NOxe, SO2e, CO2e] = emissions(elec, eNOx, eSO2, eCO2)
% MA average 2000 annual output emission rates
%eNOx = 2.005; % lb/MWh
%eSO2 = 5.569; % lbs/MWh
%eCO2 = 1293.151; % lb/MWh

% NG
%ngNOx = 0.098039216; % lb/MMBtu
%ngSO2 = 0.000588235; % lbs/MMBtu
%ngCO2 = 117.6470588; % lb/MMBtu

%elec = 100; % MWh
%ng = 100; % MBtu
%%%%%%%%%%%%%%%%%%%%%%%

% scf = 1020 Btu

NOxe = eNOx*elec; % lb
SO2e = eSO2*elec; % lb
CO2e = eCO2*elec; % lb

%NOxng = ngNOx/1020*ng; % lb
%SO2ng = ngSO2/1020*ng; % lb
%CO2ng = ngCO2/1020*ng; % lb

%NOxtot = NOxe + NOxng;
%SO2tot = SO2e + SO2ng;
%CO2tot = CO2e + CO2ng;
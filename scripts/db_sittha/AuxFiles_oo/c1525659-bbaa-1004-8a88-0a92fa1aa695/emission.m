% MA average 2000 annual output emission rates
%eNOx = 2.005; % lb/MWh
%eSO2 = 5.569; % lbs/MWh
%eCO2 = 1293.151; % lb/MWh

% NG
%ngNOx = 100; % lb/Mscf
%ngSO2 = 0.6; % lbs/Mscf
%ngCO2 = 120000; % lb/Mscf

elec = 100; % MWh
ng = 100; % MBtu
%%%%%%%%%%%%%%%%%%%%%%%

% scf = 1020 Btu

NOxe = eNOx*elec; % lb
SO2e = eSO2*elec; % lb
CO2e = eCO2*elec; % lb

NOxng = ngNOx/1020*ng; % lb
SO2ng = ngSO2/1020*ng; % lb
CO2ng = ngCO2/1020*ng; % lb

NOxtot = NOxe + NOxng;
SO2tot = SO2e + SO2ng;
CO2tot = CO2e + CO2ng;
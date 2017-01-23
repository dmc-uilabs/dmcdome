%AFR = 250; % cfm
%Tin = 70; % degF
%Tout = 17; % degF

%Wi = 44; % gr/lb dry air    (70 dry-bulb temp & 40% rel humid)
%Wo = 6; % gr/lb dry air
%%%%%%%%%%%%%%%%%%

% specific heat of air = 0.24 Btu/lb-degF
% 7000 grains of moisture in a lb
% latent heat value of water = 1061 Btu/lb 
% specific heat of water vapor = 0.45 Btu/lb-degF

% enthalpy - in Btu/lb
Hi = 0.24*Tin + Wi/7000*(1061 + 0.45*Tin);
Ho = 0.24*Tout + Wo/7000*(1061 + 0.45*Tout);

% total ventilation heat change
Qvtot = 4.5*AFR*(Hi-Ho); % Btu/h
% 4.5 = 0.075 lb/ft^3 (density of air) * 60 min/h
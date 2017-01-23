%designHeatLoad = 20000;   % Btu/h
%probMaxWaterHeatLoad = 5000 % Btu/h
%occType = 2;
%fluidType = 1;
%%%%%%%%%%%%%%%%%%%%%%%
load = designHeatLoad + probMaxWaterHeatLoad;
if occType == 1 % heavy
    oversizeFac = 1.075;
elseif occType == 2 % light
    oversizeFac = 1.175;
end
if fluidType == 1 % water
    IBRfac = 1.15;
elseif fluidType == 2 % steam
    IBRfac = 1.3;
end
size = load*oversizeFac*IBRfac;
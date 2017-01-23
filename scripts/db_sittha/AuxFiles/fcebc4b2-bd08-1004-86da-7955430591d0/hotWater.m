function [cap, stor, qh, qtot, strE] = hotWater(cityID, to, bldgType, fixtures, use)
load water supplyWaterTemp;
for i = 1:size(supplyWaterTemp,1)
    if supplyWaterTemp(i,1) == cityID
        ti = supplyWaterTemp(i,2);
        break;
    end
end

load water demands;
demand = demands(1:18,bldgType);
Gh = sum(demand.*fixtures); % gph

% heater/coil capacity
demFac = demands(19, bldgType);
cap = demFac*Gh; % probable max demand in gph

% storage tank capacity
storFac = demands(20, bldgType);
stor = storFac*Gh;

% water heating load (to be added to boiler's load)
% thus, don't need efficiency in this eqn
qh = 8.33*cap*(to- ti); % Btu/hr
% density of water is 8.33 lb/gal
% specfic heat of water is 1 Btu/lb-degF

% daily energy consumption
qtot = sum(demand.*fixtures.*use/60*8.33*(to- ti));  % Btu

% stored energy
strE = sum(stor*8.33*(to- ti)*use/60); % Btu
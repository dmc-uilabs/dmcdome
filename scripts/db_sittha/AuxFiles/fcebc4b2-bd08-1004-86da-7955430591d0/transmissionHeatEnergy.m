function [Qtot] = transmissionHeatEnergy(wallA, wallU, windowA, windowU, ceilingA, ceilingU, floorOverUnheatA, floorOverUnheatU, slabFloorEdge, slabEdgeU, bmwallA, bmwallU, bmfloorA, bmfloorU, unheatWallA, unheatWallU, heatDegHrs, bmhDegHrs)
% heat loss coefficients - all in Btu/h-degF
wallQtr_ = sum(wallA.*wallU);
windowQtr_ = sum(windowA.*windowU);
ceilingQtr_ = sum(ceilingA.*ceilingU);
floorOverUnheatQtr_ = sum(floorOverUnheatA.*floorOverUnheatU/2);
slabFloorQtr_ = sum(slabFloorEdge.*slabEdgeU);
bmwallQtr_ = sum(bmwallA.*bmwallU);
bmfloorQtr_ = sum(bmfloorA.*bmfloorU);
unheatWallQtr_ = sum(unheatWallA.*unheatWallU);

% heating energy consumption - all in Btu
wallQtr = wallQtr_*heatDegHrs;
windowQtr = windowQtr_*heatDegHrs;
ceilingQtr = ceilingQtr_*heatDegHrs;
floorOverUnheatQtr = floorOverUnheatQtr_*heatDegHrs;
slabFloorQtr = slabFloorQtr_*heatDegHrs;
unheatWallQtr = unheatWallQtr_*heatDegHrs;
bmwallQtr = bmwallQtr_*bmhDegHrs;
bmfloorQtr = bmfloorQtr_*bmhDegHrs;
Qtr = wallQtr+windowQtr+ceilingQtr+floorOverUnheatQtr+slabFloorQtr+bmwallQtr+bmfloorQtr+unheatWallQtr;
Qtot = sum(Qtr);
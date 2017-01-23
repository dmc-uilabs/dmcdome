% heat loss coefficients - all in Btu/h-degF
wallQtr_ = sum(wallA.*wallU);
windowQtr_ = sum(windowA.*windowU);
ceilingQtr_ = sum(ceilingA.*ceilingU);
floorOverUnheatQtr_ = sum(floorOverUnheatA.*floorOverUnheatU/2);
slabFloorQtr_ = sum(slabFloorEdge.*slabEdgeU);

% heating energy consumption - all in Btu
wallQtr = wallQtr_*heatDegHrs;
windowQtr = windowQtr_*heatDegHrs;
ceilingQtr = ceilingQtr_*heatDegHrs;
floorOverUnheatQtr = floorOverUnheatQtr_*heatDegHrs;
slabFloorQtr = slabFloorQtr_*heatDegHrs;
Qtr = wallQtr+windowQtr+ceilingQtr+floorOverUnheatQtr+slabFloorQtr;
Qtot = sum(Qtr);
% heat loss coefficients - all in Btu/h-degF
wallQtr_ = sum(wallA.*wallU);
windowQtr_ = sum(windowA.*windowU);
ceilingQtr_ = sum(ceilingA.*ceilingU);
floorOverUnheatQtr_ = sum(floorOverUnheatA.*floorOverUnheatU/2);
slabFloorQtr_ = sum(slabFloorEdge.*slabEdgeU);

% heat loss - all in Btu/h
wallQtr = wallQtr_*(Tin-T975);
windowQtr = windowQtr_*(Tin-T975);
ceilingQtr = ceilingQtr_*(Tin-T975);
floorOverUnheatQtr = floorOverUnheatQtr_*(Tin-T975);
slabFloorQtr = slabFloorQtr_*(Tin-T975);
Qtr = wallQtr+windowQtr+ceilingQtr+floorOverUnheatQtr+slabFloorQtr;
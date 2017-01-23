function [annualHeatInput, unitFuel, cost] = boilerFuelCost(annualHeat, annualDHW, AFUE, fuelHeatVal, unitCost)
annualHeatInput = (annualHeat+annualDHW)/AFUE;
unitFuel = annualHeatInput/fuelHeatVal;
cost = unitFuel*unitCost;
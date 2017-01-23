function [fuel, cost_, cost] = boilerFuelCost(annualHeat, annualDHW, AFUE, fuelHeatVal, unitCost, bkt)
annualHeatInput = (annualHeat+annualDHW)/AFUE;
unitFuel = annualHeatInput/fuelHeatVal;
for i = 1:12
    fuel_ = zeros(1, size(unitCost,2));
    for j=1:size(bkt,2)
        if bkt(i,j) < unitFuel(i)
            if j == 1
                fuel_(j) = bkt(i,j);
            else
                fuel_(j) = bkt(i,j)-bkt(i,j-1);
            end
            if j == size(bkt,2)
                fuel_(j+1) = unitFuel(i)-bkt(i,j);
            end
        else
            if j == 1
                fuel_(j) = unitFuel(i);
            else
                fuel_(j) = unitFuel(i) - bkt(i,j-1);
            end
            break;
        end
    end
    fuel(i,:) = fuel_;
    cost_(i,:) = unitCost(i,:).*fuel_;
end
cost = sum(cost_,2);
function [accStatNPV, accNewNPV, netSave, pbt] = NPVsaving(statCost, newCost, statCapCost, newCapCost, dctRate, lifetime)
save = sum(statCost-newCost);
accStatNPV = []; accNewNPV = [];
pbt = -1;
for i=1:lifetime
    statNPV(i) = statCost(i)/(1+dctRate)^i;
    newNPV(i) = newCost(i)/(1+dctRate)^i;
    
    accStatNPV(i) = statCapCost+sum(statNPV);    
    accNewNPV(i) = newCapCost+sum(newNPV);
    
    if (accStatNPV(i) > accNewNPV(i)) & pbt == -1
        pbt = i;
    end
end
netSave = accStatNPV(lifetime) - accNewNPV(lifetime);

x = 1:lifetime;
figure; hold;
plot(x, accStatNPV, 'ro',  x, accNewNPV, 'bo');
grid on;
xlabel('year')
ylabel('net-present-value ($)')
title('accumulated cost')
legend('statistic','new',0);
print -dtiff accCost;
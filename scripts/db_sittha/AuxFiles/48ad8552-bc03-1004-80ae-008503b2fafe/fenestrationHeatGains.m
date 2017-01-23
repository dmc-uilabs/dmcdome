function [qb, qd, qc] = fenestrationHeatGains(U, A, Af, Tin, Tout, glassID, IAC, theta, ED, Ed, Er)

load fenestrationData
% SHGC data is indexed by [0 40 50 60 70 80 hemis]
% select the associated row of SHGC data
for i=1:size(SHGC,1)
    if SHGC(i,1) == glassID
        j = i; break;        
    end
end
SHGC_D = SHGC(j,8);
SHGC_val = SHGC(j,2:7);

[SHGC_theta] = directSolHeatGainCoef(theta, SHGC_val)';  % dimensionless

% conductive - 24 hrs
for i=1:24
    if Tin(i) == -1 % uncontrolled
        qc(i,1) = 0;
    else
        qc(i,1) = U*Af*(Tout(i)-Tin(i));   % Btu/h
    end
end

% prune out negative radiation
for i=1:size(ED,1)
    if ED(i,1) < 0
        ED(i,1) = 0;
    end
    if Ed(i,1) < 0
        Ed(i,1) = 0;
    end
    if Er(i,1) < 0
        Er(i,1) = 0;
    end
end
        
% direct beam solar - 24 hrs
qb = A.*ED.*SHGC_theta;     % Btu/h

% diffuse solar - 24 hrs
qd = IAC*SHGC_D*A.*(Ed+Er);
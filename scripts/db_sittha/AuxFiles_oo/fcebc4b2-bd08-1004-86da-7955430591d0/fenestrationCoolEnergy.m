function [qbtot, qdtot, qctot, qtot, qtotR] = fenestrationCoolEnergy(U, A, Af, coolDegHrs, glassID, IAC, theta, ED, Ed, Er, Tin)
% Tin of -1 accounts for uncontrolled period 
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

% conductive - 8 3-hr periods
qc_ = U*Af*coolDegHrs;   % Btu
qctot = sum(qc_);

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
        
% direct beam solar
qb = A.*ED.*SHGC_theta;     % Btu
% diffuse solar
qd = IAC*SHGC_D*A.*(Ed+Er);

% include uncontrolled period
qbtotR = sum(qb);
qdtotR = sum(qd);

% exclude uncontrolled period
for i=1:8
    if Tin(i)==-1 % uncontrolled period
        qb(3*i-2)=0;
        qb(3*i-1)=0;
        qb(3*i)=0;
        qd(3*i-2)=0;
        qd(3*i-1)=0;
        qd(3*i)=0;
    end
end
qbtot = sum(qb);
qdtot = sum(qd);

qtot = sum(qctot+qbtot+qdtot);
qtotR = sum(qctot+qbtotR+qdtotR);
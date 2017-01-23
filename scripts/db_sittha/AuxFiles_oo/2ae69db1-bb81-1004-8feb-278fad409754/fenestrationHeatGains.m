function [qb, qd, qc] = fenestrationHeatGains(U, A, Tin, Tout, glassID, IAC, theta, ED, Ed, Er, sunrise, sunset)

load fenestrationData
% SHGC data is indexed by [0 40 50 60 70 80 hemis]
SHGC_ = SHGC(glassID,:); % select the associated row of SHGC data
SHGC_D = SHGC_(length(SHGC_));
SHGC_val = SHGC_(1:6);

[SHGC_theta] = directSolHeatGainCoef(theta, SHGC_val)';  % dimensionless

% conductive - 24 hrs
qc = U*A.*(Tout-Tin);   % Btu/h

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
        
% direct beam solar - day hrs
qb_ = A.*ED.*SHGC_theta;     % Btu/h
% 24 hrs
qb = zeros(24,1);
qb(ceil(sunrise):floor(sunset))=qb_;


% diffuse solar - day hrs
qd_ = IAC*SHGC_D*A.*(Ed+Er);
% 24 hrs
qd = zeros(24,1);
qd(ceil(sunrise):floor(sunset))=qd_;
function [Vbat, SOCn_plus] = lead_acid_battery2(Vbat_prev, SOCn_prev, Ibat, ns, k, D, SOCm, dt, T)
format long
% k = 0.8;
% D = 0.001;          % 1/h
% SOCm = 1200;        % Wh
% ns = 12;

%Vbat_prev = 24;       % V
%SOCn_prev = 0.6;
%Ibat = 0;           % A
%dt = 1;             % s
%T = 1;             % s, integration time step

% integrate
for i = T: T: dt,   % s
    if Ibat > 0 
        V1_prev = (2 + 0.148*SOCn_prev)*ns;
    else
        V1_prev = (1.926 + 0.124*SOCn_prev)*ns;
    end
    SOCn_plus = SOCn_prev*(1-D*T/3600) + (k*V1_prev*Ibat/3600/SOCm)*T;
    SOCn_prev = SOCn_plus;
end
beta = SOCn_plus;

if Ibat > 0         % charge
    V1 = (2+0.148*beta)*ns;
    R1 = (0.758 + 0.1309/(1.06-beta))*ns/SOCm;
    Vbat = V1 + Ibat*R1;
else     % discharge
    V1 = (1.926 + 0.124*beta)*ns;
    R1 = (0.19 + 0.1037/(beta-0.14))*ns/SOCm;
    Vbat = V1 + Ibat*R1;
end


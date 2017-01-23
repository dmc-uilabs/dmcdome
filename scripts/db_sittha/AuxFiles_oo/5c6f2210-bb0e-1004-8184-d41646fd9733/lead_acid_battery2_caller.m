%k = 0.8;
%D = 0.001;          % 1/h
%SOCm = 1200;        % Wh
%ns = 12;
%Vbat_prev = 24;       % V
%SOCn_prev = 0.6;
%Ibat = 0;           % A
%dt = 1;             % s
%T = 1;             % s, integration time step

[Vbat,SOCn_plus] = lead_acid_battery2(Vbat_prev,SOCn_prev, Ibat, ns, k, D, SOCm, dt, T)
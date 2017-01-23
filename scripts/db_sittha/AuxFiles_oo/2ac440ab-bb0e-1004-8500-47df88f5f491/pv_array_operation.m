% light-generated current
Gr = 1000;          % W/m^2
Tr = 298;           % K
Il = P1*G*(1-P2*(G-Gr)+P3*(Tp-Tr))

% reverse saturation current
k = 1.38065E-23;    % J/K
Io = B*Tp^3*exp(-Ego/(k*Tp))

% output current
q = 1.6022E-19      % C
res = solve('_Ip = M*Il - M*Io*(exp(q*(N*V + _Ip*Rs*N/M)/(N*A*k*Tp))-1) - (N*V+_Ip*Rs*N/M)/(N*Rsh/M)', '_Ip');
Ip = eval(res)

% absorbed solar power
Qin = alpha*G*Sp

% ground radiative loss
sigma = 5.67E-8;     % W/m^2K^4
Qground = Sp*Fpg*sigma*(eps_p*Tp^4 - eps_g*Tg^4)

% sky temperature
Ts = 0.914*Ta

% sky radiative loss
Qsky = Sp*Fps*sigma*(eps_p*Tp^4 - eps_s*Ts^4)

% radiative loss
Qrad = Qground + Qsky

% convective heat transfer coefficient
H = 1.2475*((Tp-Ta)*cos(beta))^(1/3) + 2.685*Vwind

% convective loss
Qconv = Sp*H*(Tp-Ta)

% module instanteneous efficiency
eta = eta0*(1-gamma*(Tp-Tr))

% produced power
Qelect = eta*G*Sc/100

% change in cell temperature
dTp = (Qin - Qrad - Qconv - Qelect)*dt/mCp
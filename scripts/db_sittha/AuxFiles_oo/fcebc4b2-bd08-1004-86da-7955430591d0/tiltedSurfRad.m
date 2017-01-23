function [i, I_bc, I_dc, I_rc, I_c] = tiltedSurfRad(alpha, a_s, a_w, beta, mo, C_n, rho)

I_vec = [1229.465561
1213.703182
1185.3309
1134.891287
1103.366529
1087.60415
1084.451674
1106.519005
1150.653666
1191.635851
1220.008133
1232.618037];   % W/m^2

k_vec = [0.142
0.144
0.156
0.18
0.196
0.205
0.207
0.201
0.177
0.16
0.149
0.142];

C_vec = [0.058
0.06
0.071
0.097
0.121
0.134
0.136
0.122
0.092
0.073
0.063
0.057];

i = zeros(24,1);
I_bN = zeros(24,1);
I_bc = zeros(24,1);
I_dc = zeros(24,1);
I_rc = zeros(24,1);
for j=1:24
    i(j) = (acos(cos(alpha(j)*pi/180)*cos((a_s(j)-a_w)*pi/180)*sin(beta*pi/180) ...
        + sin(alpha(j)*pi/180)*cos(beta*pi/180)))*180/pi;   % incident angle (deg)
    if alpha(j) ~= 0
        I_bN(j) = C_n*I_vec(mo)*exp(-k_vec(mo)/sin(alpha(j)*pi/180));   % beam rad (W/m^2)
    end
    val = I_bN(j)*cos(i(j)*pi/180);
    if val > 0
        I_bc(j) = val;  % beam on tilted surf
    end
    I_dh(j) = I_bN(j)*C_vec(mo);    % diffuse
    if beta ~= 90
        I_dc(j) = C_vec(mo)*I_bN(j)*(cos(beta/2*pi/180))^2;  % diffuse on tilted surf
    else    % vertical surface
        if cos(i(j)*pi/180) > -0.2
            y = 0.55+0.437*cos(i(j)*pi/180) + 0.313*(cos(i(j)*pi/180))^2;
        else
            y = 0.45;
        end
        I_dc(j) = C_vec(mo)*I_bN(j)*y;  % diffuse on tilted surf
    end
    I_rc(j) = rho*I_bN(j)*(sin(alpha(j)*pi/180)+C_vec(mo))*(sin(beta/2*pi/180))^2;
end
I_c = I_bc+I_dc+I_rc;
% convert results from W/m^2 to Btu/h-ft^2
I_bc = I_bc*0.317211;
I_dc = I_dc*0.317211;
I_rc = I_rc*0.317211;
I_c = I_c*0.317211;
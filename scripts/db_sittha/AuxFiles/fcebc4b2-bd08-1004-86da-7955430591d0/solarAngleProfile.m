function [delta_s, LST_sr, LST_ss, alpha, a_s] = solarAngleProfile(n, L, l_st, l_local)
% solar declination
delta_s = 23.45*sin(2*pi*(284+n)/365);   % degree

% sunrise and sunset solar time
h_sr = 12 - acos(-tan(L*pi/180)*tan(delta_s*pi/180))*180/pi*4/60;  % hr
h_ss = 12 + acos(-tan(L*pi/180)*tan(delta_s*pi/180))*180/pi*4/60;  % hr

% equation of time
B = 360*(n-81)/364;
ET = 9.87*sin(2*B*pi/180) - 7.53*cos(B*pi/180) - 1.5*sin(B*pi/180); % min

% sunrise and sunset local time
LST_sr = h_sr - ET/60 -(l_st-l_local)/15;    % hr
LST_ss = h_ss - ET/60 -(l_st-l_local)/15;    % hr

% solar due-east and -west time
t_e = 12 - acos(tan(delta_s*pi/180)/tan(L*pi/180))*180/pi/15;   % hr
t_w = 12 + acos(tan(delta_s*pi/180)/tan(L*pi/180))*180/pi/15;   % hr

t_s = zeros(24,1);
h_s = zeros(24,1);
alpha = zeros(24,1);
a_s = zeros(24,1);
for i = 1:24
    if i > LST_sr && i < LST_ss
        t_s(i) = i + ET/60 + (l_st - l_local)/15;   % solar time (hr)
        h_s(i) = (t_s(i) - 12)*15;  % solar hour angle (deg)
        alpha(i) = (asin(sin(L*pi/180)*sin(delta_s*pi/180) + ...
            cos(L*pi/180)*cos(delta_s*pi/180)*cos(h_s(i)*pi/180)))*180/pi; % altitude (deg)
        angle = asin(cos(delta_s*pi/180)*sin(h_s(i)*pi/180)/cos(alpha(i)*pi/180))*180/pi; % deg
        if L>delta_s && t_e<t_s(i) && t_s(i)<t_w
            a_s(i) = angle;
        elseif (L>delta_s && (t_s(i)<=t_e || t_s(i)>=t_e) && h_s(i)<=0) ...
                || (L<=delta_s && h_s(i)<=0)
            a_s(i) = -180 + abs(angle);
        elseif (L>delta_s && (t_s(i)<=t_e || t_s(i)>=t_e) && h_s(i)>0) ...
                || (L<=delta_s && h_s(i)>0)
            a_s(i) = 180 - abs(angle);
        end
    end
end
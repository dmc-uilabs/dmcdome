function [te, q, qgain, Qconv, Qr, Qtot] = roofwallCondCoolLoad(to, Et, sunrise, sunset, isLightColor, isHorizontal, isWall, U, A, ti, wallroofID, constructionID)
load CTS wallCTS;

if isLightColor == 1
    alphah0 = 0.15;
else
    alphah0 = 0.3;
end
if isHorizontal == 1
    eps = 7;    % degF
else
    eps = 0;    % degF
end

et = zeros(24,1);
et(ceil(sunrise):floor(sunset))=Et;

% sol-air temp.
te = to + alphah0*et - eps;
% conductive heat input
q = U*A*(te-ti);

% conductive heat gain
CTS_ = wallCTS(:,wallroofID);
q = flipud(q);
for i = 1:24,   % for each hour
    q24 = q(24);    % save the last item
    q = q(1:23);   % cut the last item off
    q = q';
    q = [q24 q];   % paste the last item at the beginning
    q = q';
    qgain(i) = sum(CTS_.*q/100);
end 
q = flipud(q); % qdc becomes like original one
qgain = qgain';

if isWall == 1
    rPct = 0.63; cPct = 0.37;
else
    rPct = 0.84; cPct = 0.16;
end

% convective load
Qconv = cPct*qgain;

load RTS nonsolRTS;
nonsolRTS_ = nonsolRTS(:,constructionID);

qr = flipud(qgain);
for i = 1:24,   % for each hour
    qr24 = qr(24);    % save the last item
    qr = qr(1:23);   % cut the last item off
    qr = qr';
    qr = [qr24 qr];   % paste the last item at the beginning
    qr = qr';
    Qr(i) = sum(nonsolRTS_.*qr*rPct/100);
end 
Qr = Qr';
Qtot = Qr+Qconv;
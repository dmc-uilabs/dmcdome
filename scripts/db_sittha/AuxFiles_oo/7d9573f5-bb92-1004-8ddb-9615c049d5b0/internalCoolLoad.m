function [Qconv, Qr, QtotLight, QallLight, QlatOc, QconvOc, QrOc, QtotOc, QallOc, QlatAp, QconvAp, QrAp, QtotAp, QallAp, QconvEq, QrEq, QtotEq, QallEq, Qall, QdayLight, QdayOc, QdayAp, QdayEq, Qday] = internalCoolLoad(lightUse, totLoad, lightType, dayoccup, maxPeople, activityType, applUse, applType, numAppl, equiUse, equiType, numEqui, constructionID)
% lighting heat gain
for i = 1:48
    q(i,:) = lightUse(i,:)*3.41.*totLoad/100;
end

load RTS nonsolRTS;
nonsolRTS_ = nonsolRTS(:,constructionID);

for j = 1:size(q,2) % for all cols
    if lightType(1, j) == 1
        rPct = 0.67; cPct = 0.33;
    elseif lightType(1, j) == 2
        rPct = 0.59; cPct = 0.41;
    elseif lightType(1, j) == 3
        rPct = 0.19; cPct = 0.81;
    elseif lightType(1, j) == 4
        rPct = 0.80; cPct = 0.20;
    end
    
    % convective cooling load
    Qconv(:,j) = q(25:48,j)*cPct;
    
    qr = q(:,j);
    for i = 1:24,   % for each hour (hr 25 - 48)
        qr_ = qr(i+1:i+24); % take only 24 hrs prior
        qr_ = flipud(qr_);
        Qr(i, j) = sum(nonsolRTS_.*qr_*rPct/100);
    end
end
QtotLight = Qr+Qconv;
QallLight = sum(QtotLight, 2);
QdayLight = sum(QallLight);

%%%%%%%%%%%%%%%%%%%%%
% occupatant heat gain
load heatGainRate people;

for j = 1:size(dayoccup,2) % for all cols
    heat = people(activityType(j),:); % in Btu/h

    % latent cooling load, Btu/h
    QlatOc(:,j) = dayoccup(25:48,j)*maxPeople(j)*heat(2)/100;    % heat(2) is latent heat gain

    % sensible cooling load
    QsenOc(:,j) = dayoccup(:,j)*maxPeople(j)*heat(1)/100;    % heat(1) is sensible heat gain
    
    % convective, sensible cooling load
    QconvOc(:,j) = QsenOc(25:48,j)*(1-heat(3)/100);   % heat(3) is radiant pct. of sensible heat gain
    
    % radiant, sensible cooling load
    qr = QsenOc(:,j);
    for i = 1:24,   % for each hour (hr 25 - 48)
        qr_ = qr(i+1:i+24); % take only 24 hrs prior
        qr_ = flipud(qr_);
        QrOc(i, j) = sum(nonsolRTS_.*qr_*heat(3)/10000);
    end
end
QtotOc = QlatOc + QconvOc + QrOc;
QallOc = sum(QtotOc, 2);
QdayOc = sum(QallOc);

%%%%%%%%%%%%%%%%%%%%%
% cooking appliance heat gain
load heatGainRate appl;

for j = 1:size(applUse,2) % for all cols
    heat = appl(applType(j),:);  % in Btu/h
    
    % latent cooling load, Btu/h
    QlatAp(:,j) = applUse(25:48,j)*numAppl(j)*heat(2)/100;    % heat(2) is latent heat gain

    % sensible cooling load
    QsenAp(:,j) = applUse(:,j)*numAppl(j)*heat(1)/100;    % heat(1) is sensible heat gain
    
    % convective, sensible cooling load
    QconvAp(:,j) = QsenAp(25:48,j)*(1-heat(3)/100);   % heat(3) is radiant pct. of sensible heat gain
    
    % radiant, sensible cooling load
    qr = QsenAp(:,j);
    for i = 1:24,   % for each hour (hr 25 - 48)
        qr_ = qr(i+1:i+24); % take only 24 hrs prior
        qr_ = flipud(qr_);
        QrAp(i, j) = sum(nonsolRTS_.*qr_*heat(3)/10000);
    end
end
QtotAp = QlatAp + QconvAp + QrAp;
QallAp = sum(QtotAp, 2);
QdayAp = sum(QallAp);

%%%%%%%%%%%%%%%%%%%%%
% office equipment heat gain
load heatGainRate equi;

for j = 1:size(equiUse,2) % for all cols
    heat = equi(equiType(j),:);  % equi is in W
    
    % total heat gain
    qE(:,j) = 3.41*equiUse(:,j)*numEqui(j)*heat(1)/100;     % heat(1) is total heat gain
    
    % convective cooling load
    QconvEq(:,j) = qE(25:48,j)*(1-heat(2)/100);   % heat(2) is radiant pct. of heat gain
    
    % radiant, sensible cooling load
    qr = qE(:,j);
    for i = 1:24,   % for each hour (hr 25 - 48)
        qr_ = qr(i+1:i+24); % take only 24 hrs prior
        qr_ = flipud(qr_);
        QrEq(i, j) = sum(nonsolRTS_.*qr_*heat(2)/10000);
    end
end
QtotEq = QconvEq + QrEq;
QallEq = sum(QtotEq, 2);
QdayEq = sum(QallEq);

Qall = QallLight+ QallOc+ QallAp+ QallEq;
Qday = sum(Qall);
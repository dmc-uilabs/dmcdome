function [Qconv, Qr, QtotLight, QallLight, QlatOc, QconvOc, QrOc, QtotOc, QallOc, QlatAp, QconvAp, QrAp, QtotAp, QallAp, QconvEq, QrEq, QtotEq, QallEq, Qall, QdayLight, QdayOc, QdayAp, QdayEq, Qday, QdayR] = internalCoolLoad(lightUse, totLoad, lightType, dayoccup, maxPeople, activityType, applUse, applType, numAppl, equiUse, equiType, numEqui, constructionID, Tin)
% lighting heat gain
if size(lightUse) ~=0
    for i = 1:48
        q(i,:) = lightUse(i,:)*3.41.*totLoad/100;
    end
    
    k=-1;
    load RTS nonsolRTS;
    for i=1:size(nonsolRTS,2)
        if nonsolRTS(1,i) == constructionID
            k = i; break;
        end
    end
    nonsolRTS_ = nonsolRTS(2:25,k);
    
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
else
    Qr = zeros(24,1);
    Qconv = zeros(24,1);
end
QtotLight = Qr+Qconv;
QallLight = sum(QtotLight, 2);

%%%%%%%%%%%%%%%%%%%%%
% occupatant heat gain
load heatGainRate people;

for j = 1:size(dayoccup,2) % for all cols
    k=-1;
    for i = 1:size(people,1)
        if people(i,1)==activityType(j)
            k=i; break;
        end
    end
    heat = people(k,2:4); % in Btu/h

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
if size(dayoccup) == 0
    QlatOc = zeros(24,1);
    QconvOc = zeros(24,1);
    QrOc = zeros(24,1);
end
QtotOc = QlatOc + QconvOc + QrOc;
QallOc = sum(QtotOc, 2);

%%%%%%%%%%%%%%%%%%%%%
% cooking appliance heat gain
load heatGainRate appl;

for j = 1:size(applUse,2) % for all cols
    k=-1;
    for i = 1:size(appl,1)
        if appl(i,1)==applType(j)
            k=i; break;
        end
    end
    heat = appl(k,2:4);  % in Btu/h
    
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
if size(applUse) == 0
    QlatAp = zeros(24,1);
    QconvAp = zeros(24,1);
    QrAp = zeros(24,1);
end
QtotAp = QlatAp + QconvAp + QrAp;
QallAp = sum(QtotAp, 2);
 
%%%%%%%%%%%%%%%%%%%%%
% office equipment heat gain
load heatGainRate equi;

for j = 1:size(equiUse,2) % for all cols
    k=-1;
    for i = 1:size(equi,1)
        if equi(i,1)==equiType(j)
            k=i; break;
        end
    end
    heat = equi(k,2:3);  % equi is in W
    
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
if size(equiUse) == 0
    QconvEq = zeros(24,1);
    QrEq = zeros(24,1);
end
QtotEq = QconvEq + QrEq;
QallEq = sum(QtotEq, 2);

Qall = QallLight+ QallOc+ QallAp+ QallEq;

% for energy consumption
QallLight_ = QallLight;
QallOc_ = QallOc;
QallAp_ = QallAp;
QallEq_ = QallEq;
for i=1:8
    if Tin(i)==-1 % uncontrolled period
        QallLight_(3*i-2)=0;
        QallLight_(3*i-1)=0;
        QallLight_(3*i)=0;
        QallOc_(3*i-2)=0;
        QallOc_(3*i-1)=0;
        QallOc_(3*i)=0;
        QallAp_(3*i-2)=0;
        QallAp_(3*i-1)=0;
        QallAp_(3*i)=0;
        QallEq_(3*i-2)=0;
        QallEq_(3*i-1)=0;
        QallEq_(3*i)=0;
    end
end
QdayLight = sum(QallLight_);
QdayOc = sum(QallOc_);
QdayAp = sum(QallAp_);
QdayEq = sum(QallEq_);
Qday = QdayLight+QdayOc+QdayAp+QdayEq;

% if uncontrolled period's heat gain is not neglected
QdayR = sum(QallLight)+sum(QallOc)+sum(QallAp)+sum(QallEq);
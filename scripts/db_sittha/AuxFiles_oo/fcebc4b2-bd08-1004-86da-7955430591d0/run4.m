C_n = [];
Wi = [];
for rm = 1:numRoom
    C_n = [C_n; C_n1'];
    Wi = [Wi; Wi1'];
end
% total cooling and heating loads , plus DHW heating loads (in Btu)
cityID = num2str(cityID);
dayNum = 0; allLoads = [];
% total U and areas of all rooms
walA = zeros(1,numRoom); walU = zeros(1,numRoom); winA = zeros(1,numRoom); winU = zeros(1,numRoom); ceiA = zeros(1,numRoom); ceiU = zeros(1,numRoom);
floA = zeros(1,numRoom); floU = zeros(1,numRoom); slaA = zeros(1,numRoom); slaU = zeros(1,numRoom); bmwA = zeros(1,numRoom); bmwU = zeros(1,numRoom);
bmfA = zeros(1,numRoom); bmfU = zeros(1,numRoom); uhwA = zeros(1,numRoom); uhwU = zeros(1,numRoom);
for rm = 1:numRoom
    qr = ['rmDim = rm' num2str(rm) 'Dim;'];
    eval(qr); clear qr;
    for i=1:rmDim(1,1)
        walA(i,rm) = rmDim(1,(i-1)*2+3);
        walU(i,rm) = rmDim(1,(i-1)*2+2);
    end    
    for i=1:rmDim(2,1)
        winA(i,rm) = rmDim(2,(i-1)*2+3);
        winU(i,rm) = rmDim(2,(i-1)*2+2);
    end
    for i=1:rmDim(3,1)
        ceiA(i,rm) = rmDim(3,(i-1)*2+3);
        ceiU(i,rm) = rmDim(3,(i-1)*2+2);
    end    
    for i=1:rmDim(4,1)
        floA(i,rm) = rmDim(4,(i-1)*2+3);
        floU(i,rm) = rmDim(4,(i-1)*2+2);
    end    
    for i=1:rmDim(5,1)
        slaA(i,rm) = rmDim(5,(i-1)*2+3);
        slaU(i,rm) = rmDim(5,(i-1)*2+2);
    end    
    for i=1:rmDim(6,1)
        bmwA(i,rm) = rmDim(6,(i-1)*2+3);
        bmwU(i,rm) = rmDim(6,(i-1)*2+2);
    end
    for i=1:rmDim(7,1)
        bmfA(i,rm) = rmDim(7,(i-1)*2+3);
        bmfU(i,rm) = rmDim(7,(i-1)*2+2);
    end
    for i=1:rmDim(8,1)
        uhwA(i,rm) = rmDim(8,(i-1)*2+3);
        uhwU(i,rm) = rmDim(8,(i-1)*2+2);
    end
end

past = 0;
% hot water
[wtcap, wtstor, wtqh, wtqtot, wtstrE_] = hotWater(str2num(cityID), hotwaterT, waterBldgID, waterFixtN, waterFixtUse);
for mo=1:12
    mo
    if season(mo) ~= 0  % calculate deg-hrs only if the month is in "conditioned" season
        % scan through all thermostat settings of the month
        qr = ['moThSet = mo' num2str(mo) 'Thermo;'];
        eval(qr); clear qr;
        thSetIndex = [];    % cooling & heating degree-hrs data is reset every month
        coolDH = [];
        heatDH = [];
        for i=1:size(moThSet,1)
            for j=1:size(moThSet,2)
                % if deg-hrs haven't been calculated for this thermo set
                if contain(thSetIndex, moThSet(i,j)) == 0
                    set = thermoSet(:,moThSet(i,j));
                    [cDegHrs, hDegHrs] = degHrs(cityID, mo, set');
                    thSetIndex = [thSetIndex moThSet(i,j)];
                    coolDH = [coolDH cDegHrs'];
                    heatDH = [heatDH hDegHrs'];
                end
            end
        end
        clear cDegHrs hDegHrs set i j;
    end
    
    if mo == 1
        past = 0;
    else
        past = numDays(mo-1, yr) + past;
    end
    for da = 1:numDays(mo, yr)
        da
        dayNum = dayNum+1;
        wkDay = mod(dayNum, 7);
        if wkDay == 0
            wkDay = 7;
        end
        if wkDay == 1
            wkDay0 = 7; % yesterday
        else
            wkDay0 = wkDay-1;
        end 
        wkNum = ceil(dayNum/7);
        
        qfentot(dayNum-past, mo) = 0;
        qfentotR(dayNum-past, mo) = 0; % include heat gains during unocc periods
        qrwtot(dayNum-past, mo) = 0;
        qintot(dayNum-past, mo) = 0; 
        qintotR(dayNum-past, mo) = 0; % include heat gains during unocc periods
        qtrtot(dayNum-past, mo) = 0;
        qiftot(dayNum-past, mo) = 0;
        qvetot(dayNum-past, mo) = 0;
        qvetotR(dayNum-past, mo) = 0;% include heat loss during unocc periods
        
        if season(mo) ~= 0  % calculate heat gain/loss only if the month is in "conditioned" season
            [delta_s, LST_sr, LST_ss, alpha, a_s] = solarAngleProfile(dayNum, lat, STM, long);
            clear delta_s;
            surfSet = []; surfRad = []; % radiation data is reset everyday
            
            for rm = 1:numRoom
                thIndex = getColIndex(thSetIndex, moThSet(rm, wkDay));
                cDH = coolDH(:,thIndex); % already calculated
                Tin = thermoSet(:,moThSet(rm, wkDay));
                for surf = 1:surfNum(rm,1) % if there's an exterior surface
                    qr = ['surfDat = rm' num2str(rm) 'Surf' num2str(surf) ';'];
                    eval(qr); clear qr;
                    % see if rad has already been calculated for this plane
                    plane = colIndex(surfSet, [surfDat(1,1); surfDat(1,2)]);
                    if plane == 0
                        [inc, I_bc, I_dc, I_rc, I_c] = tiltedSurfRad(alpha, a_s, surfDat(1,1), surfDat(1,2), mo, C_n(rm,mo), rho(mo));
                        surfSet = [surfSet [surfDat(1,1); surfDat(1,2)]];
                        surfRad = [surfRad [inc; I_bc; I_dc; I_rc; I_c]];
                    else
                        inc = surfRad(1:24, plane);
                        I_bc = surfRad(25:48, plane);
                        I_dc = surfRad(49:72, plane);
                        I_rc = surfRad(73:96, plane);
                        I_c = surfRad(97:120, plane);
                    end
                    
                    if surfDat(1,2) == 0
                        isHor = 1;
                    else
                        isHor = 0;
                    end   
                    [scDegHrs, shDegHrs] = solairDegHrs(I_c', LST_sr, LST_ss, surfDat(1,3), isHor, cityID, mo, Tin);
                    clear shDegHrs;
                    numFen = surfDat(2,1);
                    for i = 1:numFen % all fenestration types
                        [qbf, qdf, qcf, qtotf, qtotfR] = fenestrationCoolEnergy(surfDat(2,(i-1)*6+3), surfDat(2,(i-1)*6+4), ...
                            surfDat(2,(i-1)*6+5), cDH, surfDat(2,(i-1)*6+2), surfDat(2,(i-1)*6+6), inc, I_bc*solfilm(rm,1), ...
                            I_dc*solfilm(rm,1), I_rc*solfilm(rm,1), Tin);
                        clear qbf qdf qcf;
                        qfentot(dayNum-past, mo) = qfentot(dayNum-past, mo) + qtotf;
                        qfentotR(dayNum-past, mo) = qfentotR(dayNum-past, mo) + qtotfR;
                    end
                    clear numFen;
                    numRW = surfDat(3,1);
                    for i = 1:numRW
                        [qtotrw] = roofwallCoolEnergy(surfDat(3,(i-1)*3+2), surfDat(3,(i-1)*3+3), scDegHrs);
                        qrwtot(dayNum-past, mo) = qrwtot(dayNum-past, mo) + qtotrw;                
                    end
                end
                qr = ['rmInt = rm' num2str(rm) 'Int;'];
                eval(qr);
                qr = ['rmPatLt = rm' num2str(rm) 'PatLt;'];
                eval(qr);
                qr = ['rmPatOc = rm' num2str(rm) 'PatOc;'];
                eval(qr);
                qr = ['rmPatAp = rm' num2str(rm) 'PatAp;'];
                eval(qr);
                qr = ['rmPatEq = rm' num2str(rm) 'PatEq;'];
                eval(qr); clear qr;
                ltUse_=[]; ltUse=[]; ltLoad=[]; ltType=[];
                for lt = 1:rmInt(1,1) % all lightings
                    ltUse_ = [rmPatLt(:,(lt-1)*7+wkDay0) ; rmPatLt(:,(lt-1)*7+wkDay)];
                    ltUse = [ltUse ltUse_];
                    ltLoad = [ltLoad rmInt(1,(lt-1)*2+3)];
                    ltType = [ltType rmInt(1,(lt-1)*2+2)];
                end
                ocUse_=[]; ocUse=[]; ocLoad=[]; ocType=[];
                for oc = 1:rmInt(2,1) % all activities
                    ocUse_ = [rmPatOc(:,(oc-1)*7+wkDay0) ; rmPatOc(:,(oc-1)*7+wkDay)];
                    ocUse = [ocUse ocUse_];
                    ocLoad = [ocLoad rmInt(2,(oc-1)*2+3)];
                    ocType = [ocType rmInt(2,(oc-1)*2+2)];
                end
                apUse_=[]; apUse=[]; apLoad=[]; apType=[];
                for ap = 1:rmInt(3,1) % all appliance
                    apUse_ = [rmPatAp(:,(ap-1)*7+wkDay0) ; rmPatAp(:,(ap-1)*7+wkDay)];
                    apUse = [apUse apUse_];
                    apLoad = [apLoad rmInt(3,(ap-1)*2+3)];
                    apType = [apType rmInt(3,(ap-1)*2+2)];
                end
                eqUse_=[]; eqUse=[]; eqLoad=[]; eqType=[];
                for eq = 1:rmInt(4,1) % all equipment
                    eqUse_ = [rmPatEq(:,(eq-1)*7+wkDay0) ; rmPatEq(:,(eq-1)*7+wkDay)];
                    eqUse = [eqUse eqUse_];
                    eqLoad = [eqLoad rmInt(4,(eq-1)*2+3)];
                    eqType = [eqType rmInt(4,(eq-1)*2+2)];
                end
                inconst = ceil(const(rm)/3)+18; % get internal construction ID from general construction ID
                [Qconv, Qr, QtotLight, QallLight, QlatOc, QconvOc, QrOc, QtotOc, QallOc, QlatAp, QconvAp, QrAp, QtotAp, QallAp, QconvEq, QrEq, QtotEq, QallEq, Qall, QdayLight, QdayOc, QdayAp, QdayEq, QdayInt, QdayIntR] = internalCoolLoad(ltUse, ltLoad, ltType, ocUse, ocLoad, ocType, apUse, apType, apLoad, eqUse, eqType, eqLoad, inconst, Tin);
                clear Qconv Qr QtotLight QallLight QlatOc QconvOc QrOc QtotOc QallOc QlatAp QconvAp QrAp QtotAp QallAp QconvEq QrEq QtotEq QallEq Qall QdayLight QdayOc QdayAp QdayEq;
                qintot(dayNum-past, mo) = qintot(dayNum-past, mo) + QdayInt;
                qintotR(dayNum-past, mo) = qintotR(dayNum-past, mo) + QdayIntR;
                % heat losses
                hDH = heatDH(:,thIndex); % already calculated
                bmhDegHr = zeros(8,1);
                if bmwA(1,rm) ~= 0 % for basement wall
                    [bmcDegHr, bmhDegHr] = degHrsOffset(cityID, mo, thermoSet(:,moThSet(rm,wkDay)), bmToff);
                    clear bmcDegHr;
                    bmhDegHr = bmhDegHr';
                end
                [Qtottr] = transmissionHeatEnergy(walA(:,rm), walU(:,rm), winA(:,rm), winU(:,rm), ceiA(:,rm), ceiU(:,rm), floA(:,rm), floU(:,rm), slaA(:,rm), slaU(:,rm), bmwA(:,rm), bmwU(:,rm), bmfA(:,rm), bmfU(:,rm), uhwA(:,rm), uhwU(:,rm), hDH, bmhDegHr);
                qtrtot(dayNum-past, mo) = qtrtot(dayNum-past, mo) + Qtottr;
                if numSto(rm) > 0 % above ground (infiltration is possible)
                    [Qinf, totQinf] = infiltrationHeatEnergy(leak((rm-1)*2+1,2:leak((rm-1)*2+1,1)+1), leak((rm-1)*2+2,2:leak((rm-1)*2+1,1)+1), numSto(rm), shield(rm), wind(mo), hDH);
                    clear Qinf;
                else
                    totQinf = 0
                end
                qiftot(dayNum-past, mo) = qiftot(dayNum-past, mo) + totQinf;
                if (AFR(rm) ~= 0) && (size(ocUse,1) ~= 0)
                    [qtotven, qtotvenR] = ventilationHeatEnergy(AFR(rm), hDH, Wi(rm,mo), cityID, wkNum, Tin, ocUse(25:48,1));
                else
                    qtotven = 0;
                    qtotvenR = 0;
                end
                qvetot(dayNum-past, mo) = qvetot(dayNum-past, mo) + qtotven;
                qvetotR(dayNum-past, mo) = qvetotR(dayNum-past, mo) + qtotvenR;
            end % of room
        end % of if conditionned
    end % of day

    % hot water
    qwttot(1,mo) = wtqtot*numDays(mo, yr);
    wtStrE(1,mo) = wtstrE_*numDays(mo, yr); % to be used for standby loss
end % of month

fig1 = figure; bar([sum(qfentotR+qrwtot+qintotR)' sum(qtrtot+qiftot+qvetotR)']); title('total, actual heat gain and loss');
xlabel('month'); ylabel('(BTU)'); legend('heat gain', 'heat loss');
print -djpeg -r300 heatGainLoss;

fig2 = figure; bar([sum(qfentotR)' sum(qrwtot)' sum(qintotR)']); title('total, actual heat gains');
xlabel('month'); ylabel('(BTU)'); legend('fenestrations', 'roofs and walls', 'internal sources', 0);

fig3 = figure; bar([sum(qtrtot)' sum(qiftot)' sum(qvetotR)']); title('total, actual heat losses');
xlabel('month'); ylabel('(BTU)'); legend('transmission', 'infiltration', 'ventilation', 0);

fig4 = figure; bar([sum(qfentot+qrwtot+qintot)' sum(qtrtot+qiftot+qvetot)']); title('heat gain and loss that affect energy consumption');
xlabel('month'); ylabel('(BTU)'); legend('heat gain', 'heat loss');
for mo = 1:12
    if season(mo) == 1 % heating
        heatE(mo) = sum(qtrtot(:,mo)+qiftot(:,mo)+qvetot(:,mo)) ...
            - sum(qfentot(:,mo)+qrwtot(:,mo)+qintot(:,mo));
        coolE(mo) = 0;
    elseif season(mo) == 2 % cooling
        heatE(mo) = 0;
        coolE(mo) = sum(qfentot(:,mo)+qrwtot(:,mo)+qintot(:,mo)) ...
            - sum(qtrtot(:,mo)+qiftot(:,mo)+qvetot(:,mo));
    elseif season(mo) ==3 % intermediate
        heatE(mo) = sum(qtrtot(:,mo)+qiftot(:,mo)+qvetot(:,mo));
        coolE(mo) = sum(qfentot(:,mo)+qrwtot(:,mo)+qintot(:,mo));
        if heatE(mo) > coolE(mo)
            heatE(mo) = sum(qtrtot(:,mo)+qiftot(:,mo)+qvetot(:,mo)) ...
                - sum(qfentot(:,mo)+qrwtot(:,mo)+qintot(:,mo));
            coolE(mo) = 0;
        else
            heatE(mo) = 0;
            coolE(mo) = sum(qfentot(:,mo)+qrwtot(:,mo)+qintot(:,mo)) ...
                - sum(qtrtot(:,mo)+qiftot(:,mo)+qvetot(:,mo));
        end
    elseif season(mo) ==0 % non-conditioned
        heatE(mo) = 0;
        coolE(mo) = 0;
    end
    % actual consumption
    if heatE(mo) < 0
        accheatE(mo) = 0;
    else
        accheatE(mo) = heatE(mo);
    end
    if coolE(mo) < 0
        acccoolE(mo) = 0;
    else
        acccoolE(mo) = coolE(mo);
    end
end
fig5 = figure; bar([coolE' heatE']); title('net heating and cooling loads');
xlabel('month'); ylabel('(BTU)'); legend('cooling', 'heating');
fig6 = figure; bar(qwttot'); title('net domestic hot water heating load');
xlabel('month'); ylabel('(BTU)'); legend('hot water');

annHeat = sum(sum(accheatE))
annCool = sum(sum(acccoolE))
annHW = sum(sum(qwttot))

allLoads = [accheatE; acccoolE; qwttot];
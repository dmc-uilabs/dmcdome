clear
load RTS;
solRTS_ = solRTS(:,constructionID);
nonsolRTS_ = nonsolRTS(:,constructionID);

if hasShade == 0    % no shade
    % no shade -> solar radiant cooling load from direct beam
    qb = flipud(qb);
    for i = 1:24,   % for each hour
        qb24 = qb(24);    % save the last item
        qb = qb(1:23);   % cut the last item off
        qb = qb';
        qb = [qb24 qb];   % paste the last item at the beginning
        qb = qb';
        Qb(i) = sum(solRTS_.*qb/100);
    end 
    qb = flipud(qb); % qb becomes like original one
    Qb=Qb';
    
    % nonsolar radiant cooling load from diffuse and conductive only
    qdc = qd+qc;
else    % with shade
    % nonsolar radiant cooling load from direct beam, diffuse, and conductive
    qdc = qd+qc+qb;
    Qb=zeros(24,1);
end
Qconv = 0.37*qdc; % 37% of diffuse and conductive is convective
qdc = 0.63*qdc; % 63% of diffuse and conductive is radiant
qdc = flipud(qdc);
for i = 1:24,   % for each hour
    qdc24 = qdc(24);    % save the last item
    qdc = qdc(1:23);   % cut the last item off
    qdc = qdc';
    qdc = [qdc24 qdc];   % paste the last item at the beginning
    qdc = qdc';
    Qdcr(i) = sum(nonsolRTS_.*qdc/100);
end 
qdc = flipud(qdc); % qdc becomes like original one
Qdcr=Qdcr';
Qdc=Qdcr+Qconv;

Qtot = Qb+Qdc;
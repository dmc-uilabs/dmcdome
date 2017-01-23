%to = [76
 %   76
  %  75
   % 74
    %74
%    74
%    75
%    77
%    80
%    83
%    87
%    90
%    93
%    94
%    95
%    94
%    93
%    91
%    87
%    85
%    83
%    81
%    79
%    77];

%Et = [0.0010501941842182567
%    11.978599233769627
%    23.120345374191462
%    31.392176975147972
%    37.81773264894289
%    43.794343335488776
%    58.215539506722465
%    125.06655356980939
%    161.8368708383562
%    193.24803590622318
%    205.0898774540456
%    194.2535886096037
%    158.27303257528837
%    93.49450132602979
%    4.374715576697032];

%sunrise = 4.886;
%sunset = 19.316;

%isLightColor = 1;
%isHorizontal = 0;
%isWall = 1; % or roof

%U = 0.068;   % Btu/h-ft^2-degF
%A = 100;     % ft^2

%ti = ones(24,1)*75; % degF

%wallroofID = 1;
%constructionID = 1;
[te, q, qgain, Qconv, Qr, Qtot] = roofwallCondCoolLoad(to, Et, sunrise, sunset, isLightColor, isHorizontal, isWall, U, A, ti, wallroofID, constructionID);
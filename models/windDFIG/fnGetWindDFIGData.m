function [gD,tD,objNum] = fnGetWindDFIGData(iFreq, iLine,iNumT,iSpeed)
%% This function accepts inputs from the Dome Matlab Model and gives the outputs so that the userinterface
%  can produce plots for the Grid Data Acquisition block and Wind Turbine Acquisition block
%
% Inputs : ( All inputs were defined as Enumeration types in DOME  (Should match what Simulation was done for)
%  iFreq:      Frequency, used in sevearl blocks  [50 60];
%  iLine:      Line Length for the 10km Line Block [ 10 50 100];
%  iNumT:      Number of Trbines in the Wind turbine Double Fed Generator Block [2 3 4 5 6];
%  iSpeed:     Starting wind speend in the Wind Speed Block  [4 5 6 7 8]
%
% Outputs: 
%  gD:    This Matrix has numrows as determined by the x1_data size and number of columns as 9. Column 9 contains x-axis data for plots
%  tD:    This Matrix has numrows as determined by the w1_data size and number of columns as 9. Column 9 contains x-axis data for plots
% % engevalstring() function  in the DLL function expects this in one line !!!!


%% Initialize the outputs to empty
gD = [];
tD = [];
objNum = 1;
matFileName = 'power_wind_dfig_object_dome.mat';
% 
% Search  the container And locate the object selected from the  Dome User Interface matfileName passed as a parmaeter gave problems
load(matFileName) ; 
objNum=size(myMapObj,1) -1;
disp(objNum)
%
for jj=1:size(myMapObj,1);obj = myMapObj(jj);objNum = obj.objNum;freq = obj.freq;line = obj.lineLen;nT = obj.numTurbines;speed = obj.startWindSpeed;if (isequal(freq,iFreq) && isequal(line,iLine) && isequal(nT,iNumT) && isequal(speed,iSpeed));    indexObjNum = objNum;end;end;
% Fetch the Obj based on indexObjNum
objNum = indexObjNum;
obj=myMapObj(indexObjNum);
nR = size(obj.gridData.x1_data,1);
gD = zeros(nR,9);
gD(:,1) = obj.gridData.x1_data(:,1);
gD(:,2) = obj.gridData.x2_data(:,1);
gD(:,3) = obj.gridData.x3_data(:,1);
gD(:,4) = obj.gridData.x4_data(:,1);
gD(:,5) = obj.gridData.x5_data(:,1);
gD(:,6) = obj.gridData.x6_data(:,1);
gD(:,7) = obj.gridData.x7_data(:,1);
gD(:,8) = obj.gridData.x8_data(:,1);
gD(:,9) = obj.gridData.x_time(:,1);
tD = zeros(nR,9);
tD(:,1) = obj.turbineData.w1_data(:,1);
tD(:,2) = obj.turbineData.w2_data(:,1);
tD(:,3) = obj.turbineData.w3_data(:,1);
tD(:,4) = obj.turbineData.w4_data(:,1);
tD(:,5) = obj.turbineData.w5_data(:,1);
tD(:,6) = obj.turbineData.w6_data(:,1);
tD(:,7) = obj.turbineData.w7_data(:,1);
tD(:,8) = obj.turbineData.w8_data(:,1);
tD(:,9) = obj.turbineData.w_time(:,1);




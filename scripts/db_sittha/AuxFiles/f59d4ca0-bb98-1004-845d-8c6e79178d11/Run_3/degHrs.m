function [cDegHrs, hDegHrs] = degHrs(cityID, month, Tin)
cityID = num2str(cityID);
if month == 1
    month = 'jan';
    days = 31;
elseif month == 2
    month = 'feb';
    days = 28.25;
elseif month == 3
    month = 'mar';
    days = 31;
elseif month == 4
    month = 'apr';
    days = 30;
elseif month == 5
    month = 'may';
    days = 31;
elseif month == 6
    month = 'jun';
    days = 30;
elseif month == 7
    month = 'jul';
    days = 31;
elseif month == 8
    month = 'aug';
    days = 31;
elseif month == 9
    month = 'sep';
    days = 30;
elseif month == 10
    month = 'oct';
    days = 31;
elseif month == 11
    month = 'nov';
    days = 30;
elseif month == 12
    month = 'dec';
    days = 31;
end
city = ['load weather' cityID ' ' month];
eval(city);
bin = eval(month);
for i=1:8
    temp = 1000; j = 1;
    while temp ~= Tin(i)
        j = j+1;
        temp = bin(j,1);
    end
    % cooling deg hrs (when temp > Tin)
    cHr = bin(2:j-1,i+1);
    cDeg = (bin(2,1)-Tin(i):-1:1)';
    cDegHrs(i) = sum(cDeg.*cHr)/days;
    % heating deg hrs (when temp < Tin)
    hHr = bin(j+1:size(bin,1),i+1);
    hDeg = (1:1:Tin(i)-bin(size(bin,1),1))';
    hDegHrs(i) = sum(hDeg.*hHr)/days;
end
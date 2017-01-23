function [cDegHrs, hDegHrs] = degHrsOffset(cityID, month, Tin, Toff)
%% use for when the outdoor temp needs to be offset (reduced) by Toff
%% such as for heat loss through basement wall or floor
%% easier to shift (increase) Tin by Toff --> same results
Tin = Tin + Toff;
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
top = bin(2,1);
bot = bin(size(bin,1),1);
for i=1:8
    if Tin(i) > top
        cDegHrs(i) = 0;
        hHr = [zeros(Tin(i)-top-1,1); bin(2:size(bin,1),i+1)];
        hDeg = (1:1:Tin(i)-bot)';
        hDegHrs(i) = sum(hDeg.*hHr)/days;
    elseif Tin(i) < bot
        hDegHrs(i) = 0;
        cHr = [bin(2:size(bin,1),i+1); zeros(bot-Tin(i)-1,1)];
        cDeg = (bin(2,1)-Tin(i):-1:1)';
        cDegHrs(i) = sum(cDeg.*cHr)/days;
    else
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
end
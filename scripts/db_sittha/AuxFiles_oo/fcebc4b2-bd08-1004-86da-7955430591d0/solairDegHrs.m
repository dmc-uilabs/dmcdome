function [cDegHrs, hDegHrs] = solairDegHrs(qt, sunrise, sunset, isLightColor, isHorizontal, cityID, month, Tin)
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

% sol-air temp.
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

% divide qt into 8 intervals
for i=1:8
    if Tin(i) == -1 % uncontrolled, no need for calculation
        shift(i) = 0;
    else
        q_(i) = sum(qt(i*3-2:i*3));
        if (i*3-3 < sunrise) && (sunrise < i*3)
            q_(i) = q_(i) - qt(ceil(sunrise))*(sunrise-floor(sunrise));
        elseif (i*3-3 < sunset) && (sunset < i*3)
            q_(i) = q_(i) + qt(floor(sunset))*(sunset-floor(sunset));
        end
        q(i) = q_(i)/3; % avg over 3-hr period
        
        %te = to + alphah0*et - eps;
        shift(i) = alphah0*q(i) - eps;  % sol-air temp shift
    end
end
shift = round(shift);

% expand temp bin upward
fstRow = bin(1,:);
hiTemp = bin(2,1);
bin = bin(2:size(bin,1),:);
bin = [zeros(max(shift),9); bin];
bin = [fstRow; bin];
bin(2:max(shift)+1,1) = (hiTemp+max(shift):-1:hiTemp+1)';

% expand temp bin downward if necessary
hasNegShift = 0;
for i=1:8
    if shift(i)<0
        hasNegShift = 1;
        break;
    end
end
if hasNegShift == 1
    size_ = size(bin,1);
    lowTemp = bin(size_,1);
    bin = [bin; zeros(max(-shift),9)];
    bin(size_+1:size(bin,1),1) = (lowTemp-1:-1:lowTemp-max(-shift))';
end

% shift columns in the bin
top = max(shift)+2;
bot = size(bin,1)-max(-shift);
for i=2:9
    if Tin(i-1) == -1 % uncontrolled, no need for calculation
        shift(i) = 0;
    else
        col = bin(top:bot,i);
        bin(2:size(bin,1),i)=0;
        bin(top-shift(i-1):bot-shift(i-1),i)=col;
    end
end

top = bin(2,1);
bot = bin(size(bin,1),1);
for i=1:8
    if Tin(i) == -1 % uncontrolled indoor temp
        cDegHrs(i) = 0;
        hDegHrs(i) = 0;
    elseif Tin(i) > top
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
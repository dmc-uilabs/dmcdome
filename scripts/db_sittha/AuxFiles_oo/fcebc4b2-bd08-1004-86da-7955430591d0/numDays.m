function [days] = numDays(month, year)
if month == 1
    month = 'jan';
    days = 31;
elseif month == 2
    month = 'feb';
    if mod(year,4)==0
        days = 29;
    else
        days = 28;
    end    
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
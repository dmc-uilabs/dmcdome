function [SHGC_t] = directSolHeatGainCoef(theta, SHGC_val)

for i = 1:size(theta)
    if theta(i) > 90
        SHGC_t(i) = 0;
    elseif theta(i) > 80
        SHGC_t(i) = (SHGC_val(6)-SHGC_val(5))/10*(theta(i)-80) + SHGC_val(6);
    elseif theta(i) > 70  
        SHGC_t(i) = (SHGC_val(6)-SHGC_val(5))/10*(theta(i)-70) + SHGC_val(5);
    elseif theta(i) > 60
        SHGC_t(i) = (SHGC_val(5)-SHGC_val(4))/10*(theta(i)-60) + SHGC_val(4);
    elseif theta(i) > 50
        SHGC_t(i) = (SHGC_val(4)-SHGC_val(3))/10*(theta(i)-50) + SHGC_val(3);
    elseif theta(i) > 40
        SHGC_t(i) = (SHGC_val(3)-SHGC_val(2))/10*(theta(i)-40) + SHGC_val(2);
    else
        SHGC_t(i) = (SHGC_val(2)-SHGC_val(1))/40*theta(i) + SHGC_val(1);
    end
end
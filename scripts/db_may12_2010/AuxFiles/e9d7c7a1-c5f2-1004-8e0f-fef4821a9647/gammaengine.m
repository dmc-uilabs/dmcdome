function [hp torque cost] = gammaengine(octane_rating)
if (octane_rating < 87)
    hp = 0;
    torque = 0;
elseif (octane_rating < 89)
    hp = 200;
    torque = 200;
elseif (octane_rating >= 89)
    hp = 205;
    torque = 208;
end
cost = 2300;
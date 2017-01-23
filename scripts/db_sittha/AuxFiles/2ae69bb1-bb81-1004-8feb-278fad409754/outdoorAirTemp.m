function [outdoorTemp] = outdoorAirTemp(drbulbTemp, dailyRange)
tempRangePct = [87 92 96 99 100 98 93 84 71 56 39 23 11 3 0 3 10 21 34 47 58 68 76 82]';
outdoorTemp = drbulbTemp-dailyRange.*tempRangePct./100;
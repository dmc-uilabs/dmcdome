function [i] = getColIndex(arr, val)
i = 0;
for j = 1:size(arr,2)
    if arr(1,j) == val
        i = j;
        break;
    end
end
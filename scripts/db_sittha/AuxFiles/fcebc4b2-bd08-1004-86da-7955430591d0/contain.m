function [y] = contain(arr, ele)
y=0;
for i=1:size(arr,1)
    for j=1:size(arr,2)
        if arr(i,j) == ele
            y=1;
            break;
        end
    end
end
function [ans] = rowIndex(arr, row)
y = 0; ans = 0;
for i = 1:size(arr,1)
    for j = 1:size(arr,2)
        if arr(i,j) ~= row(1,j)
            y = 0;
            break;
        else
            y = 1;
        end
    end
    if y == 1
        ans = i;
        break
    end
end
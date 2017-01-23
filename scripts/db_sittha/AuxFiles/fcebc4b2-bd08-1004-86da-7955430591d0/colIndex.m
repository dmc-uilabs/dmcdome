function [ans] = colIndex(arr, col)
y = 0; ans = 0;
for i = 1:size(arr,2)
    for j = 1:size(arr,1)
        if arr(j,i) ~= col(j,1)
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
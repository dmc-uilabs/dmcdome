function [contact_length deflection_nom load_per_length] = sealfeann(seal_width, seal_height, seal_thickness, seal_gap)

global best_net is_first_run;

train_data = load('traindata.txt');
input_data = train_data(:,[1:4])';
origin_target_data = train_data(:,[5:7])';
input_ranges = minmax(input_data);
output_ranges = minmax(origin_target_data);
net = newff(input_ranges, [4 3], {'logsig','logsig'});

% normalize target data
target_data = [];
target_data(1,:) = origin_target_data(1, :) ./ output_ranges(1, 2);
target_data(2,:) = origin_target_data(2, :) ./ output_ranges(2, 2);
target_data(3,:) = origin_target_data(3, :) ./ output_ranges(3, 2);

% compared_target_data(1,:) = sim_target_data(1, :) .* output_ranges(1, 2);
% compared_target_data(2,:) = sim_target_data(2, :) .* output_ranges(2, 2);
% compared_target_data(3,:) = sim_target_data(3, :) .* output_ranges(3, 2);

train_input = input_data(:,1:2:length(input_data));
testset.P = input_data(:,2:2:length(input_data));

train_target = target_data(:,1:2:length(target_data));
testset.T = target_data(:,2:2:length(target_data));

[rows cols] = size(is_first_run)
if (rows == 0 && cols == 0)
    best_net_error =+ inf;
    for n = 1:2
        %net.trainParam.show = 1;
        [net training_record] = train(net, train_input, train_target, [], [], testset);
        net_error = training_record.vperf(end); % gets the performance in the last epoch

        if (net_error < best_net_error) 
            best_net = net;
            best_net_error = net_error;		
            disp 'This network is the best so far!'
        else
            disp 'This network is worse than the best!'
        end;
        
        
    end;
    
    is_first_run = [1];
end;
    

sim_target_data = sim(best_net, [seal_width seal_height seal_thickness seal_gap]');
sim_target_data_after_normalization = sim_target_data .* output_ranges(:, 2);

contact_length = sim_target_data_after_normalization(1);
deflection_nom = sim_target_data_after_normalization(2);
load_per_length = sim_target_data_after_normalization(3);

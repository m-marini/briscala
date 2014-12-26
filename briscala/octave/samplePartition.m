function [XL YL XV, YV, XT, YT] = samplePartition(X, Y, p_train, p_valid, p_test)
%samplePartition Implements the partition of samples into 3 subset
% the training set, the validation set and the test set.
%   [Train, Valid, Test] = samplePartition(X, Y, p_train, p_valid, p_test)
% Divide the X sample set into three subset with number of episode proportional to the parameters
% p_train, p_vlaid, p_test
% The X samples are organized in episode each end of episode are identified by
% a row with first value to -1 (no action)

m = size(X, 1);
Idx = randperm(m);
X = X(Idx, :);
Y = Y(Idx, :);

ntr = round(m * p_train / (p_train + p_valid + p_test));
nv = round(m * p_valid / (p_train + p_valid + p_test));

XL  = X(1 : ntr, : );
YL  = Y(1 : ntr, : );

XV  = X(ntr + 1 : ntr + nv, : );
YV  = Y(ntr + 1 : ntr + nv, : );

XT  = X(ntr + nv + 1 : end, : );
YT  = Y(ntr + nv + 1 : end, : );

endfunction
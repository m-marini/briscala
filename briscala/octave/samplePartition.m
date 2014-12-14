function [Train, Valid, Test] = samplePartition(X, p_train, p_valid, p_test)
%samplePartition Implements the partition of samples into 3 subset
% the training set, the validation set and the test set.
%   [Train, Valid, Test] = samplePartition(X, p_train, p_valid, p_test)
% Divide the X sample set into three subset with number of episode proportional to the parameters
% p_train, p_vlaid, p_test
% The X samples are organized in episode each end of episode are identified by
% a row with first value to -1 (no action)

m = size(X, 1);

% Find indexes of episode ends
endEpisodeIndex  = find(X(:, 1) == -1);

n = size(endEpisodeIndex,1);

ntr = round(n * p_train / (p_train + p_valid + p_test));
nv = round(n * p_valid / (p_train + p_valid + p_test));

printf("Training set of %d episodes\n", ntr);
printf("Validation set of %d episodes\n", nv);
printf("Test set of %d episodes\n", n - ntr - nv);

Train  = X(1 : endEpisodeIndex(ntr + 1) - 1, : );
Valid  = X(endEpisodeIndex(ntr + 1) : endEpisodeIndex(ntr + nv + 1) - 1, : );
Test  = X(endEpisodeIndex(ntr + nv + 1) : end, : );
endfunction
function [Train, Valid, Test] = samplePartition(X, p_train, p_valid, p_test)
%samplePartition Implements the partition of samples into 3 subset
% the training set, the validation set and the test set.
%   [Train, Valid, Test] = samplePartition(X, p_train, p_valid, p_test)
% Divide the X sample set into three subset with number of episode proportional to the parameters
% p_train, p_vlaid, p_test
% The X samples are organized in episode each end of episode are identified by
% a row with first value to -1 (no action)

m = size(X, 1);

episodeId = zeros(m, 1);

% Find indexes of episode ends
endEpisodeIndex  = find(X(:, 1) == -1 == 1);

n = size(endEpisodeIndex,1);

% for each episode assign the ordinal number
for i = 1 : n - 1
	  episodeId(endEpisodeIndex(i) : endEpisodeIndex(i + 1) - 1) = i;
endfor
episodeId(endEpisodeIndex(end) :end) = n;

ntr = round(n * p_train / (p_train + p_valid + p_test));
nv = round(n * p_valid / (p_train + p_valid + p_test));

printf("Training set of %d episodes\n", ntr);
printf("Validation set of %d episodes\n", nv);
printf("Test set of %d episodes\n", n - ntr - nv);

Train  = X(find(episodeId <= ntr), : );
Valid  = X(find(episodeId > ntr & episodeId <= (nv+ntr)), : );
Test  = X(find(episodeId > (nv+ntr)), : );
endfunction
function learningCurve(to, step)
% learningCurve applies the learning algorithm, computes the error for training set and validation set
% for different sample set size specified by parameters
% to the last sample size
% step the step of each sample size

% Load dataset

printf("Load dataset ...\n");
load -ascii "../briscola.mat";

ep= find(briscola(:, 1) == -1);

me = size(ep,1);
to = min(to, me);

EpisodeSize = step : step: to;
n = length(EpisodeSize);

lambda = 0;
s2 = 80;

StepSize = (ep(EpisodeSize)) - 1;

E = zeros(n, 2);

for i = 1 : n
	
	% Partition samples
	[Train, Valid, Test] = samplePartition(briscola( 1 : StepSize(i), :), 60, 40, 0);

	[X, Y] = toFeatures(Train);
	[XV, YV] = toFeatures(Valid);

	options = optimset('MaxIter', 100);

	s1 = size(X, 2);
	s3 = size(Y, 2);

	epsilon = sqrt( 6 / (s1 + s2) );
	np = (s1 + 1) * s2 + (s2 + 1) * s3;

	costFunction = @(p) nnLogisticCostFunction(p, s2, X, Y, lambda);
	[params, cost] = fmincg(costFunction, rand(np, 1) * 2 * epsilon - epsilon, options);

	E(i, : ) = [ errorFunction(X, Y, params, s2)  errorFunction(XV, YV, params, s2) ];
endfor

plot(EpisodeSize, E(:, 1), ";Training error;", EpisodeSize, E(:, 2), ";Valdation error;");

endfunction
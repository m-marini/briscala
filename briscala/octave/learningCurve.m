function learningCurve(to, step, s2, lambda)
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

StepSize = (ep(EpisodeSize)) - 1;

E = zeros(n, 2);

options = optimset('MaxIter', 50);

for i = 1 : n
	printf("Processing %d episodes, %d steps ...\n", EpisodeSize(i), StepSize(i));
	
	[ X Y ] = toFeatures(briscola( 1 : StepSize(i), :));
	
	% Partition samples
	[XL, YL, XV, YV, XT, YT] = samplePartition(X, Y, 60, 40, 0);

	s1 = size(X, 2);
	s3 = size(Y, 2);

	epsilon = sqrt( 6 / (s1 + s2) );
	np = (s1 + 1) * s2 + (s2 + 1) * s3;

	costFunction = @(p) nnLogisticCostFunction(p, s2, XL, YL, lambda);
	[params, cost] = fmincg(costFunction, rand(np, 1) * 2 * epsilon - epsilon, options);

	[W1, W2] = rollParms(params, s1, s2, s3);
	E(i, : ) = [ sqrt(cost(end) * 2)  errorFunction(XV, YV, W1, W2) ];
endfor

plot(EpisodeSize, E(:, 1), ";Training error;", EpisodeSize, E(:, 2), ";Valdation error;");

printf("Final training error = %e\n", E(end, 1));
printf("Final validation error = %e\n", E(end, 2));

endfunction
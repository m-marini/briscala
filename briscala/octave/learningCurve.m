function learningCurve(from, to, step)
% Load dataset

printf("Loading dataset ...\n");
load -ascii "../briscola.mat";

ep= find(briscola(:, 1) == -1);

me = size(ep,1);
to = min(to, me);

M = from : step: to;
E = zeros(size(M,1), 2);

lambda = 0;
s2 = 2;

for i = 1 : length(M)
	
	% Partition samples
	[Train, Valid, Test] = samplePartition(briscola( 1 : ep(M(i)), :), 60, 40, 0);

	% gamma = return discount rate
	gamma = 0.965936;
	[X, Y] = convert(Train, gamma);
	[XV, YV] = convert(Valid, gamma);

	options = optimset('MaxIter', 100);

	s1 = size(X, 2);
	s3 = size(Y, 2);

	epsilon = sqrt( 6 / (s1 + s2) );
	np = (s1 + 1) * s2 + (s2 + 1) * s3;

	costFunction = @(p) nnCostFunction(p, s2, X, Y, lambda);
	[params, cost] = fmincg(costFunction, rand(np, 1) * 2 * epsilon - epsilon, options);

	E(i, : ) = [ errorFunction(X, Y, params, s2), errorFunction(XV, YV, params, s2) ];
endfor

plot(M, E);

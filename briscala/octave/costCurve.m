function costCurve(episodeCount, s2, s3, lambda = 0, nIter= 50)
% costCurve plots the cost function for each iteration of
% learning algorithm.
%
% costCurve(episodeCount, s2, s3, lambda = 0, nIter= 50)
%  episodeCount: number of episode
%  s2: number of 1st hidden layer neurons
%  s3: number of 2nd hidden  layer ne
%  lambda: regularization learning parameter
%  nIter: number of learning iteration 

% Load dataset
filename = "../briscola.mat";
printf("Load dataset ...\n");
S = load("-ascii",  filename);

ep= find(S(:, 1) == -1);

me = size(ep,1);
to = min(episodeCount, me);

if to == me
	m = size(S, 1);
else
	m = (ep(to+1)) - 1;
endif

options = optimset('MaxIter', nIter);

[ X Y ] = toFeatures(S(1 : m, : ));
	
s1 = size(X, 2);
s4 = size(Y, 2);

epsilon = sqrt( 6 / (s1 + s2 + s3) );
np = (s1 + 1) * s2 + (s2 + 1) * s3 + (s3 + 1) * s4;

costFunction = @(p) nnLogisticCostFunction(p, s2, s3, X, Y, lambda);
[params, cost] = fmincg(costFunction, rand(np, 1) * 2 * epsilon - epsilon, options);

plot(cost, ";Cost function;", sqrt(cost * 2), ";Error function;");

endfunction
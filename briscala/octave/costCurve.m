function costCurve(filename, s2, s3, mx = -1, c = 1e3, nIter=50)
% costCurve plots the cost function for each iteration of
% learning algorithm.
%
%  costCurve(filename, s2, s3, mx = -1, c = 1e3, nIter=50)
%   filename: filename
%   s2: number of 1st hidden layer neurons
%   s3: number of 2nd hidden  layer ne
%   mx: max number of samples
%   c: regularization parameter
%   nIter: number of learning iteration 

% Load dataset
printf("Load dataset %s ...\n", filename);
[X, Y] = loadFeatures(filename);
[m1 s1] = size(X);
s4 = size(Y, 2);

if (mx > 0)
	m = min(mx, m1);
else
	m = m1;
endif
X = X(1 : m, : );
Y = Y(1 : m, : );

options = optimset('MaxIter', nIter)

epsilon = sqrt( 6 / (s1 + s2 + s3) );
np = (s1 + 1) * s2 + (s2 + 1) * s3 + (s3 + 1) * s4;

costFunction = @(p) nnLogisticCostFunction(p, s2, s3, X, Y, c);

printf("Minimizing over %d samples\n", m);
[params, cost] = fmincg(costFunction, rand(np, 1) * 2 * epsilon - epsilon, options);

plot(cost, ";Cost function;");

endfunction
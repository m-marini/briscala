function learningCurve(filename, to, step, s2, s3, c = 1e3, nIter= 50)
% learningCurve plots the learning curve for given samples size.
% The learning curve shows the training error and validation error
% for different number of samples.
%
% learningCurve(filename, to, step, s2, s3, c = 1e3, nIter= 50)
% filename: the filenane with the samples
%  to: max number of samples
%  step: step increment of samples length
%  s2: number of 1st hidden layer neurons
%  s3: number of 2nd hidden  layer neurons
%  c: regularization parameter
%  nIter: number of learning iteration 

% Load dataset
printf("Load dataset %s ...\n", filename);
[X, Y] = loadFeatures(filename);
m = size(X, 1);

printf("Loaded %d samples ...\n", m);

to = min(to, m);

StepSize = step : step : to;
n = length(StepSize);
E = zeros(n, 2);

options = optimset('MaxIter', nIter);

for i = 1 : n
	printf("Processing %d samples ...\n", StepSize(i));
	
	% Partition samples
	[XL, YL, XV, YV, XT, YT] = samplePartition(X(1 : StepSize(i), : ), Y(1 : StepSize(i), : ), 60, 40, 0);

	s1 = size(X, 2);
	s4 = size(Y, 2);

	epsilon = sqrt( 6 / (s1 + s2 + s3) );
	np = (s1 + 1) * s2 + (s2 + 1) * s3 + (s3 + 1) * s4;

	costFunction = @(p) nnLogisticCostFunction(p, s2, s3, XL, YL, c);
	[params, cost] = fmincg(costFunction, rand(np, 1) * 2 * epsilon - epsilon, options);

	[W1 W2 W3] = rollParms(params, s1, s2, s3, s4);
	E(i, : ) = [ errorFunction(XL, YL, W1, W2, W3) errorFunction(XV, YV, W1, W2, W3) ];
endfor

plot(StepSize, E(:, 1), ";Training error;", StepSize, E(:, 2), ";Valdation error;");

printf("Final training error = %e\n", E(end, 1));
printf("Final validation error = %e\n", E(end, 2));

endfunction
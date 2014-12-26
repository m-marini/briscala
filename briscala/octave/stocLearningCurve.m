function stocLearningCurve(filename, to, step, s2, s3, alpha=1e-3, c = 1e3, nIter= 30)
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
[m s1] = size(X);

printf("Loaded %d samples ...\n", m);

to = min(to, m);

StepSize = step : step : to;
n = length(StepSize);
E = zeros(n, 2);

options = optimset('MaxIter', nIter);

s4 = size(Y, 2);

for i = 1 : n	
	% Partition samples
	[XL, YL, XV, YV, XT, YT] = samplePartition(X(1 : StepSize(i), : ), Y(1 : StepSize(i), : ), 60, 40, 0);

	epsilon = sqrt( 6 / (s1 + s2 + s3) );
	np = nnParameters(s1, s2, s3, s4);
	W = rand(np, 1) * 2 * epsilon - epsilon;
	W = fminscg(W, s2, s3, XL, YL, alpha, c, nIter);
	E(i, : ) = [ errorFunctionP(XL, YL, W, s2, s3) errorFunctionP(XV, YV, W, s2, s3) ];
endfor

plot(StepSize, E(:, 1), ";Training error;", StepSize, E(:, 2), ";Valdation error;");

printf("Final training error = %e\n", E(end, 1));
printf("Final validation error = %e\n", E(end, 2));

endfunction
function main(id) 
% Load dataset

printf("Loading dataset ...\n");

filename = sprintf("best-%d-%6.6d.mat", id, floor(rand() * 1000000));
printf("Result file = %s\n", filename);

load "test.mat";

% ------------------------------------------
% Train the network
% ------------------------------------------
%
% lambda = regularization parameter
% noHiddens = number of hidden neurons
%
options = optimset('MaxIter', 100);

printf("Training the networks ...\n");

% Train the networks with different parameters varying
% lambda and number of hidden neurons

minLambda = 0.01
maxLambda = 0.01
minNoHidden = 10
maxNoHidden = 10

s1 = size(X, 2);
s3 = size(Y, 2);

validationError = 1e300;
bestLambda = 0;
bestNoHidden = 0;

qlambda = log(minLambda);
mlambda = log(maxLambda/ minLambda);
qnh = log(minNoHidden);
mnh = log(maxNoHidden / minNoHidden);

% Randomize initial values    
for i = 1 : 1
	% init the data
	lambda = exp(rand() * mlambda + qlambda) ;
	s2 = floor( exp( rand() * mnh + qnh ) );
	epsilon = sqrt( 6 / (s1 + s2) );
	np = (s1 + 1) * s2 + (s2 + 1) * s3;
        
	% Vary regularization parameters

% ---------------------------------------------
% Learning
% ---------------------------------------------

	printf("Network  lambda = %g, noHidden = %d\n", lambda, s2);
	printf("    current best network lambda = %g, noHidden = %d\n", bestLambda, bestNoHidden);
	printf("    rms error on validation set = %g \n", validationError);
	%   costFunction = @(p) nnCostFunction(p, s2, X, Y, lambda);
	%   [params, cost] = fmincg(costFunction, rand(np, 1) * 2 * epsilon - epsilon, options);
	params = rand(np, 1) * 2 * epsilon - epsilon;
	for k = 1 : 5
		for j = 1 : size(X, 1)
			printf("%d\n", j);
			[params, cost] = fminscg(params, s2, X(j, : ), Y(i, : ), lambda, 0.01);
		endfor
	endfor
% ---------------------------------------------
% Validation
% ---------------------------------------------

	err =  errorFunction(XV, YV, params, s2);
	if err < validationError
		validationError = err;
		bestLambda = lambda;
		bestNoHidden = s2;
		bestParams = params;

		trainingError = errorFunction(X, Y, bestParams, bestNoHidden);
		testError = errorFunction(XT, YT, bestParams, bestNoHidden);
% ---------------------------------------------
% Testing
% ---------------------------------------------
	printf("Testing network ...\n");
	printf("\n");
	printf("Lambda = %g\n", bestLambda);
	printf("# hidden unit = %d\n", bestNoHidden);
	printf("RMS error on training set = %g\n", trainingError);
	printf("RMS error on validation set = %g\n", validationError);
	printf("RMS error on test set = %g\n", testError);
	save(filename, "trainingError", "validationError", "testError", "bestLambda", "bestNoHidden", "bestParams" );

	endif
endfor
endfunction
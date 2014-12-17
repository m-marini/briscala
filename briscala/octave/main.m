function main(filename, ...
	inFile  = "features.mat",
	noTrainIter = 1000, ...
	noTestIter = 100, ...
	minLambda = 1e-3, ...
	maxLambda = 1, ...
	minNoHidden = 10, ...
	maxNoHidden = 20 ) 
%  main(filename, ...
%	inFile  = "features.mat",
%	noTrainIter = 1000, ...
%	noTestIter = 100, ...
%	minLambda = 1e-3, ...
%	maxLambda = 1, ...
%	minNoHidden = 10, ...
%	maxNoHidden = 20 ) 

% lambda = regularization parameter
% noHiddens = number of hidden neurons


% filename = sprintf("best-%d-%6.6d.mat", id, floor(rand() * 1000000));
printf("Result file = %s\n", filename);

% Load dataset
printf("Loading dataset %s ...\n", inFile);
load(inFile);

printf("Training set %d samples...\n", size(XL, 1));
printf("Validation set %d samples...\n", size(XV, 1));
printf("Test set %d samples...\n", size(XT, 1));

% ------------------------------------------
% Train the network
% ------------------------------------------
options = optimset('MaxIter', noTrainIter);

printf("Training the networks ...\n");

% Train the networks with different parameters varying
% lambda and number of hidden neurons

s1 = size(XL, 2);
s4 = size(YL, 2);

trainingError = 0;
validationError = 1e300;
testError = 0;
bestLambda = 0;
bestNoHidden = 0;

qlambda = log(minLambda);
mlambda = log(maxLambda/ minLambda);
qnh = log(minNoHidden);
mnh = log(maxNoHidden / minNoHidden);

for i = 1 : noTestIter
	% init the data
	lambda = exp(rand() * mlambda + qlambda) ;
	s2 = floor( exp( rand() * mnh + qnh ) );
	s3 = s2;
	epsilon = sqrt( 6 / (s1 + s2 + s3) );
	np = (s1 + 1) * s2 + (s2 + 1) * s3 + (s3 + 1) * s4;

% ---------------------------------------------
% Training
% ---------------------------------------------

	printf("\n");
	printf("Training network  lambda = %g, noHidden = %d\n", lambda, s2);
	printf("Best ntework:\n");
	printf("  Lambda = %g\n", bestLambda);
	printf("  Hidden units = %d\n", bestNoHidden);
	printf("  RMS error on training set = %g\n", trainingError);
	printf("  RMS error on validation set = %g\n", validationError);
	printf("  RMS error on test set = %g\n", testError);
	Params = rand(np, 1) * 2 * epsilon - epsilon;
	costFunction = @(p) nnLogisticCostFunction(p, s2, s3, XL, YL, lambda);
	[Params Cost] = fmincg(costFunction, Params, options);

%	for k = 1 : 5
%		for j = 1 : size(X, 1)
%			printf("%d\n", j);
%			[params, cost] = fminscg(params, s2, X(j, : ), Y(i, : ), lambda, 0.01);
%		endfor
%	endfor

% ---------------------------------------------
% Validation
% ---------------------------------------------

	[W1, W2, W3] = rollParms(Params, s1, s2, s3, s4);
	err =  errorFunction(XV, YV, W1, W2, W3);
	if err < validationError
		validationError = err;
		bestLambda = lambda;
		bestNoHidden = s2;
		BestParams = Params;

		trainingError = sqrt(Cost(end) * 2);
		testError = errorFunction(XT, YT, W1, W2, W3)
% ---------------------------------------------
% Testing
% ---------------------------------------------
		save(filename, "trainingError", "validationError", "testError", "bestLambda", "bestNoHidden", "BestParams" );
	endif
endfor

endfunction

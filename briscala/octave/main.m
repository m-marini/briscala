function main(filename, ...
	inFile  = "states.mat",
	noTrainIter = 300, ...
	noTestIter = 100, ...
	minC = 1, ...
	maxC = 1e3, ...
	minNoHidden = 10, ...
	maxNoHidden = 20 ) 
%  main(filename, ...
%	inFile  = "states.mat",
%	noTrainIter = 300, ...
%	noTestIter = 100, ...
%	minC = 1, ...
%	maxC = 1e3, ...
%	minNoHidden = 10, ...
%	maxNoHidden = 20 ) 

% c = regularization parameter
% noHiddens = number of hidden neurons


% filename = sprintf("best-%d-%6.6d.mat", id, floor(rand() * 1000000));
printf("Result file = %s\n", filename);

% Load dataset
printf("Load dataset %s ...\n", inFile);
[X, Y] = loadFeatures(inFile);
m = size(X, 1);

printf("Loaded %d samples ...\n", m);

[XL, YL, XV, YV, XT, YT] = samplePartition(X, Y, 60, 20, 20);

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

fe = length( find( fnmatch( filename, readdir( "." ) ) ) );
if fe > 0
	printf("Loading %s file ...\n", filename);
	load(filename);
	s2 = s3 = bestNoHidden;
	[W1, W2, W3] = rollParms(BestParams, s1, s2, s3, s4);
	validationError =  errorFunction(XV, YV, W1, W2, W3)
	trainingError = errorFunction(XL, YL, W1, W2, W3)
	testError = errorFunction(XT, YT, W1, W2, W3)
else
	trainingError = 0;
	validationError = 1e300;
	testError = 0;
	bestC = 0;
	bestNoHidden = 0;
endif
qc = log(minC);
mc = log(maxC/ minC);
qnh = log(minNoHidden);
mnh = log(maxNoHidden / minNoHidden);

for i = 1 : noTestIter
	% init the data
	c = exp(rand() * mc + qc) ;
	s2 = floor( exp( rand() * mnh + qnh ) );
	s3 = s2;
	epsilon = sqrt( 6 / (s1 + s2 + s3) );
	np = (s1 + 1) * s2 + (s2 + 1) * s3 + (s3 + 1) * s4;

% ---------------------------------------------
% Training
% ---------------------------------------------

	printf("\n");
	printf("Training network  C = %g, noHidden = %d\n", c, s2);
	printf("Best network:\n");
	printf("  C = %g\n", bestC);
	printf("  Hidden units = %d\n", bestNoHidden);
	printf("  RMS error on training set = %g\n", trainingError);
	printf("  RMS error on validation set = %g\n", validationError);
	printf("  RMS error on test set = %g\n", testError);
	Params = rand(np, 1) * 2 * epsilon - epsilon;
	costFunction = @(p) nnLogisticCostFunction(p, s2, s3, XL, YL, c);
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
		bestC = c;
		bestNoHidden = s2;
		BestParams = Params;

		trainingError = errorFunction(XL, YL, W1, W2, W3)
% ---------------------------------------------
% Testing
% ---------------------------------------------
		testError = errorFunction(XT, YT, W1, W2, W3)
		save(filename, "trainingError", "validationError", "testError", "bestC", "bestNoHidden", "BestParams" );
	endif
endfor

endfunction

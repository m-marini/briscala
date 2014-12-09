% Load dataset
load -ascii "../briscola.mat";

% Partition samples
[Train, Valid, Test] = samplePartition(briscola, 60, 20, 20);

% ------------------------------------------
% Train the network
% ------------------------------------------
%
% gamma = return discount rate
% lambda = regularization parameter
% noHiddens = number of hidden neurons
%

printf("Training the networks ...");

noHiddens = 20;
gamma = 0.965936;
lambda = 0.01;
options = optimset('MaxIter', 100);

% init the data
[X, Y] = convert(Train, gamma);
s1 = size(X, 2);
s2 = noHiddens;
s3 = size(Y, 2);
np = (s1 + 1) * s2 + (s2 + 1) * s3;
epsilon = sqrt( 6 / (s1 + s2) );

% learn
costFunction = @(p) nnCostFunction(p, noHiddens, X, Y, lambda);
[parms, cost] = fmincg(costFunction, rand(np, 1) * 2 * epsilon - epsilon, options);

% plot cost function
plot(cost);
printf("Final cost function %e\n", cost(end));
printf("Root mean square error on training set = %e\n",
 errorFunction(X, Y, parms, noHiddens));

% ---------------------------------------------
% Validate lerning parameters
% ---------------------------------------------

electNetParms = parms;

% ---------------------------------------------
% Test lerning results
% ---------------------------------------------

printf("Testing elected network ...");
[X, Y] = convert(Test, gamma);

printf("Root mean square error on test set = %e\n",
 errorFunction(X, Y, electNetParms, noHiddens));

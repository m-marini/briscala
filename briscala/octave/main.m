% Load dataset
clear all;
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
gamma = 0.965936;
options = optimset('MaxIter', 100);

printf("Training the networks ...");

% Train the networks with different parameters varying
% lambda and number of hidden neurons
lambdaSet = [0, 0.01, 0.03, 0.1, 0.3, 1, 3, 10];
noHiddenSet = [10];
noRandomization = 1;

noLambdas = length(lambdaSet);
noHiddens = length(noHiddenSet);

[X, Y] = convert(Train, gamma);
[XV, YV] = convert(Valid, gamma);
[XT, YT] = convert(Test, gamma);

s1 = size(X, 2);
s3 = size(Y, 2);

bestValidError = 1e300;

% Vary regularization parameters
for i = 1 : noLambdas
    lambda = lambdaSet(i);
    % Vary number of hidden units
    for j = 1 : noHiddens
        
        % init the data
        s2 = noHiddenSet(j);
        epsilon = sqrt( 6 / (s1 + s2) );
        np = (s1 + 1) * s2 + (s2 + 1) * s3;
        
        % Randomize initial values    
        for k = 1 : noRandomization
            % learn
            printf("Network #%d, lambda = %g, noHidden = %d ...\n",k, lambda, s2);
            costFunction = @(p) nnCostFunction(p, s2, X, Y, lambda);
            [params, cost] = fmincg(costFunction, rand(np, 1) * 2 * epsilon - epsilon, options);
        
            % validate
            err =  errorFunction(XV, YV, params, s2);
            if err < bestValidError
                printf("  Better root mean square error on validation set = %g ...\n",err);
                bestLearnError = err;
                bestValidError = err;
                bestLambda = lambda;
                bestNoHidden = s2;
                bestParams = params;
            endif
        endfor
    endfor
endfor

% ---------------------------------------------
% Test results
% ---------------------------------------------

printf("%s Testing elected network ...\n", strftime ("%r (%Z) %A %e %B %Y", localtime (time ())));
printf("\n");
printf("Lambda = %g\n", bestLambda);
printf("# hidden unit = %d\n", bestNoHidden);
printf("Root mean square error on learning set = %g\n", errorFunction(X, Y, bestParams, bestNoHidden));
printf("Root mean square error on validation set = %g\n", bestValidError);
printf("Root mean square error on test set = %g\n", errorFunction(XT, YT, bestParams, bestNoHidden));

save "bestNet.mat"  bestLambda bestNoHidden  bestParams;
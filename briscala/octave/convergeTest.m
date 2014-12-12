function convergeTest() 
% Load dataset

printf("Loading dataset ...\n");

load "test.mat";

% ------------------------------------------
% Train the network
% ------------------------------------------
%
% lambda = regularization parameter
% noHiddens = number of hidden neurons
%

printf("Training the networks ...\n");

% Train the networks with different parameters varying
% lambda and number of hidden neurons

[m, s1] = size(X);
s3 = size(Y, 2);

% Randomize initial values    
% init the data
alpha = 10e-3;
lambda = 0;
s2 = 80;
noIter = 100;

epsilon = sqrt( 6 / (s1 + s2) );
np = (s1 + 1) * s2 + (s2 + 1) * s3;

% ---------------------------------------------
% Learning
% ---------------------------------------------

params = rand(np, 1) * 2 * epsilon - epsilon;
J = zeros(m * noIter, 1);
for j = 1 : noIter
	for i = 1 : m
		[params, cost] = fminscg(params, s2, X(i, : ), Y(i, : ), lambda, alpha);
		J(i+(j-1) *m ) = cost;
	endfor
endfor

plot(mobileAvg(J, m));

endfunction
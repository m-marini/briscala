function convergenceCurve(filename, s2, s3, costCurve=true, mx = -1, alpha = 10e-3, c = 1e3, noIter = 5) 
% convergenceCurve(filename, s2, s3,mx = -1, alpha = 10e-3, c = 1e3, noIter = 5) 
%  filename: the sample filename
%  s2: number of neruons on 1st hidden layer
%  s3: number of neruons on 2nd hidden layer
% mx: max number of samples
%  alpha: learning coefficient
%  c: regurlarization coefficient
%  noIter: number of iterations

% Load dataset
printf("Loading dataset %s ...\n", filename);
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

% Randomize initial values    
% init the data
epsilon = sqrt( 6 / (s1 + s2) );
np = nnParameters(s1, s2, s3, s4);

% ---------------------------------------------
% Learning
% ---------------------------------------------

W = rand(np, 1) * 2 * epsilon - epsilon;
printf("Testing %d samples\n", m);

[W J E] = fminscg(W, s2, s3, X, Y, alpha, c, noIter);
nc = size(J,1);

XAxis = [m : nc] / m;
if costCurve
	plot(XAxis, mobileAvg(J, m));
else
	plot(XAxis, mobileAvg(E, m));
endif
	
endfunction
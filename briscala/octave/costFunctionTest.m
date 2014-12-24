function costFunctionTest(m = 100, ...
	s2 = 2, ...
	s3 = 2, ...
	c = 1e3)
% Test the cost function and gradient
%  costFunctionTest(m = 100, ...
%	s1 = 1, ...
%	s2 = 2, ...
%	s3 = 2, ...
%	s4 = 2, ...
%	c = 1e3)

X = (0 : m - 1)' * 2 * pi / (m-1);
Y = sin(2*X);

s1 = size(X, 2);
s4 = size(Y, 2);

np = s2 * (s1 + 1) + s3 * (s2 + 1) + s4 * (s3 + 1);

epsilon = 1e-6;

rms = zeros(1, np);
for i = 1 : m
    nn_parms = rand(np, 1) * 2 * 1 - 1;
    [J grad] = nnLogisticCostFunction(nn_parms, s2, s3, X, Y, c);
    for j = 1 : np
        p1 = nn_parms;
        p1(j) += epsilon;
        [J2 grad2] = nnLogisticCostFunction(p1, s2, s3, X, Y, c);
        grad1 = (J2 -J) / epsilon;
        rms(j) += (grad(j) - grad1) ^ 2;
    endfor
endfor
rms = sqrt(rms / m);
[W1, W2, W3] = rollParms(rms, s1, s2, s3, s4);

printf("rms = %e\n", sum(rms) / length(rms))

endfunction
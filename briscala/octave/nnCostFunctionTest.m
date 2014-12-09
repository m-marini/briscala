%
% Test the cost function and gradient
%

m = 100;
s1 = 1;
s2 = 2;
s3 = 1;
lambda = 0;

X = (0 : m - 1)' * 2 * pi / (m-1);
Y = sin(2*X);
np = s2 * (s1 + 1) + s3 * (s2 + 1);

epsilon = 1e-3;

rms = 0;
for i = 1 : m
    nn_parms = rand(np, 1) * 2 * 1 - 1;
    [J grad] = nnCostFunction(nn_parms, s1, s2, s3, X, Y, lambda);
    for j = 1 : np
        p1 = nn_parms;
        p1(j) += epsilon;
        [J2 grad2] = nnCostFunction(p1, s1, s2, s3, X, Y, lambda);
        grad1 = (J2 -J) / epsilon;
        rms += (grad(j) - grad1) ^ 2;
    endfor
endfor
rms = sqrt(rms / m);
printf("RMS= %f\n", rms);
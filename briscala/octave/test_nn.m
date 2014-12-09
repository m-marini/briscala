m = 100;
s1 = 1;
s2 = 20;
s3 = 1;

X = (0 : m - 1)' * 2 * pi / (m-1);
Y = sin(2*X);

plot (X, Y);
pause

np = s2 * (s1 + 1) + s3 * (s2 + 1);

epsilon = sqrt( 6 / (s1 + s2 ) ) ;

init_nn_parms = rand(np, 1) * 2 * epsilon - epsilon;

options = optimset('MaxIter', 1000);

lambda = 0.01;

costFunction = @(p) nnCostFunction(p, s1, s2, s3, X, Y, lambda);

[nn_parms, cost] = fmincg(costFunction, init_nn_parms, options);

printf("Cost %f\n", cost(end));
plot(cost)
pause

Yp = nnpredict(X, nn_parms, s1, s2, s3);

plot(Yp)
pause
load -ascii "../briscola.mat";

S = samplePartition(briscola, 2, 18 , 80);

[X Y] = toFeatures(S);

X(end + 1, : ) = X(end, :);
Y(end + 1, : ) = !Y(end, :);

[X, Y] = stateValues(X, Y);
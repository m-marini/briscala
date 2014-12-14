load -ascii "../briscola.mat";

S = samplePartition(briscola, 2, 18 , 80);

[X Y] = toFeatures(S);

[X, Y] = statFeatures(X, Y);
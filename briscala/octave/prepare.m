% Load dataset
clear all;
load -ascii "../briscola.mat";

% Partition samples
[Train, Valid, Test] = samplePartition(briscola, 60, 20, 20);

% gamma = return discount rate
gamma = 0.965936;
[X, Y] = convert(Train, gamma);
[XV, YV] = convert(Valid, gamma);
[XT, YT] = convert(Test, gamma);

save ("test.mat", "X", "Y", "XV", "YV", "XT", "YT");

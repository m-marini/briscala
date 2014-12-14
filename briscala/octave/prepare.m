function prepare() 
% prepare implements the transformation of episode set into the training, validation and test feature set
%   it reads the briscola.mat file and writes the results in test.mat file continins
%   X, Y training features set, 
%   XV, YV validation features set, 
%   XT, YT test features set, 
%  gamma = return discount rate

inFile = "../briscola.mat";
outFile = "test.mat";
trainingPref = 60;
validationPref = 20;
testPref = 20;
gamma = 0.965936;

% Load dataset
load("-ascii", inFile);

% Partition samples
[Train, Valid, Test] = samplePartition(briscola, trainingPref, validationPref, testPref);

[X, Y] = toFeatures(Train);
[XV, YV] = toFeatures(Valid);
[XT, YT] = toFeatures(Test);

save (outFile, "X", "Y", "XV", "YV", "XT", "YT");

endfunction
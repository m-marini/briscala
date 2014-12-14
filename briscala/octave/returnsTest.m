inFile = "../briscola.mat";
trainingPref = 60;
validationPref = 20;
testPref = 20;
gamma = 0.965936;

% Load dataset
load("-ascii", inFile);
briscola = removeFinal(briscola);

% Partition samples
[Train, Valid, Test] = samplePartition(briscola, trainingPref, validationPref, testPref);

S = split(afterStates(Train));

X = returns(S)

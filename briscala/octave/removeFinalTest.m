inFile = "../briscola.mat";
trainingPref = 60;
validationPref = 20;
testPref = 20;
gamma = 0.965936;

% Load dataset
load("-ascii", inFile);

briscola(1:10, 1: 10)

X = removeFinal(briscola);

X(1:10, 1: 10)
function prepare() 
% prepare implements the transformation of episode set into the training, validation and test feature set
%   it reads the briscola.mat file and writes the results in test.mat file continins
%   X, Y training features set, 
%   XV, YV validation features set, 
%   XT, YT test features set, 

inFile = "../briscola.mat";
outFile = "test.mat";
trainingPref = 60;
validationPref = 20;
testPref = 20;

% Load dataset
load("-ascii", inFile);

[ X Y ] = toFeatures(briscola);
	
% Partition samples
[XL, YL, XV, YV, XT, YT] = samplePartition(X, Y, 60, 40, 0);

save (outFile, "XL", "YL", "XV", "YV", "XT", "YT");

endfunction
function prepare(inFile = "../briscola.mat", outFile = "features.mat", trainingPref = 60, validationPref = 20, testPref = 20) 
% prepare transforms episode set into the training, validation and test feature set.
% The number of episode for each set is proportional to the relative preference parameters.
%
% prepare(inFile = "../briscola.mat", outFile = "features.mat", trainingPref = 60, validationPref = 20, testPref = 20) 
%  inFile: input filename
%  outFile: output filename
%  trainingPref: preference for training set
%  validationPref: preference for validation set
%  testPref: preference for test set

% Load dataset
printf("Loading %s ...\n", inFile);
S = load("-ascii", inFile);

printf("Converting to features ...\n", inFile);
[ X Y ] = toFeatures(S);
	
% Partition samples
printf("Partitioning samples ...\n", inFile);
[XL, YL, XV, YV, XT, YT] = samplePartition(X, Y, trainingPref, validationPref, testPref);

save (outFile, "XL", "YL", "XV", "YV", "XT", "YT");

endfunction
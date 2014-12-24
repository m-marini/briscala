function prepareFeatures( ...
	inFile = "../briscola-10.mat", ...
	stateFile = "states.mat", ...
	actionFile="actions.mat") 
% prepareFeatures transforms inpute file into the state features file and action features file\
%
%  function prepareFeatures( ...
%	inFile = "../briscola-10.mat", ...
%	stateFile = "states.mat", ...
%	actionFile="actions.mat") 
%   inFile: the input file
%   stateFile: the state file
%   actionFile: the action file 

% Load dataset
printf("Loading file %s ...\n", inFile);
[ X Y QX QY] = toFeatureIndexes(inFile);
	
printf("Saving state function %s ...\n", stateFile);
n = 6 * 10 + 5 * 30;
save (stateFile, "n", "X", "Y");

printf("Saving action function %s ...\n", actionFile);
n = 6 * 10 + 5 * 30 + 3;
X = QX;
Y = QY;
save (actionFile, "n", "X", "Y");

endfunction
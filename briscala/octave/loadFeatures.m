function [X Y] = loadFeatures(filename = "states.mat")
% loadFeatures reads the filename and returns the value function features
%  [X Y] = loadFeatures(filename = "states.mat")
%   filename: the filename
% 
%  X: are the features
%  Y: are the values

load(filename);

X = toFeatures(X, n);

endfunction
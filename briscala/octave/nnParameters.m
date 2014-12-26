function n = nnParameters(s1, s2, s3, s4) 
% nnParamters computes the number of parameter
% for a two hidden layer neural network
%
%  n = nnParamters(s1, s2, s3, s4) 
%   s1: number of input neurons
%   s2: number of 1st hidden layer neurons
%   s3: number of 2nd hidden layer neurons
%   s4: number of output neurons

n = (s1 + 1) * s2 + (s2 + 1) * s3 + (s3 + 1) * s4;

endfunction

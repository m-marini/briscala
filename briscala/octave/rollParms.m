function [W1, W2] = rollParms(params, s1, s2, s3)
%rollParms Implements the function for rolling
%neural network parameters from a vector.
%   [W1 W2] = rollParms(params, s1, s2, s3)
%  The  parameters for the neural network are "rolled" from the vector
%  params
%

% Reshape nn_params back into the parameters Theta1 and Theta2, the weight matrices
% for our 2 layer neural network

W1 = reshape(params(1:s2 * (s1 + 1)), s2, (s1 + 1));
W2 = reshape(params((1 + (s2 * (s1 + 1))):end), s3, (s2 + 1));

endfunction

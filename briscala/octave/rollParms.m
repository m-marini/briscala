function [W1, W2, W3] = rollParms(Parms, s1, s2, s3, s4)
%rollParms Implements the function for rolling
%neural network parameters from a vector.
%   [W1 W2] = rollParms(params, s1, s2, s3)
%  The  parameters for the neural network are "rolled" from the vector
%  params
%

% Reshape nn_params back into the parameters Theta1 and Theta2, the weight matrices
% for our 2 layer neural network

n1 = s2 * (s1 + 1);
n2 = s3 * (s2 + 1);

W1 = reshape(Parms(1: n1), s2, s1 + 1);
W2 = reshape(Parms(1 + n1 : n1 + n2), s3, s2 + 1);
W3 = reshape(Parms(1 + n1 + n2 : end), s4, s3 + 1);

endfunction

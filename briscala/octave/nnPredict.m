function Y = nnPredict(X, W1, W2)
%NNCOST Implements the neural network cost function for a two layer
%neural network which performs regression
%   [J grad] = NNCOSTFUNCTON(nn_params, n1, n2, n3, X, y, lambda)
%   computes the cost and gradient of the neural network. The
%   parameters for the neural network are "unrolled" into the vector
%   nn_params and need to be converted back into the weight matrices. 
% 
%   The returned parameter grad should be a "unrolled" vector of the
%   partial derivatives of the neural network.
%

% Reshape nn_params back into the parameters Theta1 and Theta2, the weight matrices
% for our 2 layer neural network
[m, n1] = size(X);
n2 = size(W1, 1);
n3 = size(W2, 1);

% X = m by n1
% Y = m by n3
% W1 = n2 by (n1 + 1)
% W2 = n3 by (n2 + 1)

% A1 ? m by n1 + 1
A1 = [ ones(m, 1) X ];

% Z2 = m by n2
Z2 = A1 * W1';

% A2 = m by n2+1
A2 = [ ones(m, 1) sigmoid(Z2) ];

%A3 = m by n3
Y = sigmoid(A2 * W2');

endfunction

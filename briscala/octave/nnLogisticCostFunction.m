function [J grad] = nnLogisticCostFunction(params, ...
                                   noHiddens, ...
                                   X, Y, lambda)
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
n2 = noHiddens;
n3 = size(Y, 2);

[W1 W2] = rollParms(params, n1, n2, n3);

% Setup some useful variables 
         
J = 0;

W1_grad = zeros(size(W1));
W2_grad = zeros(size(W2));

% X = m by n1
% Y = m by n3
% W1 = n2 by (n1 + 1)
% W2 = n3 by (n2 + 1)

% A1 ? m by n1 + 1
X1 = [ ones(m, 1) X ];

% Z2 = m by n2
Z2 = X1 * W1';

% A2 = m by n2+1
A2 = sigmoid(Z2);
X2 = [ ones(m, 1) A2];

%A3 = m by n3+1
Z3 = X2 * W2';
A3 = sigmoid(Z3);

% Delta3 = m by n3
Delta3 = A3 - Y;
J = (sum(sum( Delta3 .^2 )) + (sum(sum(W1(:, 2 : end) .^ 2)) + sum(sum(W2(:, 2 : end) .^ 2))) * lambda) / 2 / m;

% Delta2 = m by n2 + 1
DZ3 = Delta3 .* A3 .* (1 - A3);

% W2_grad = n3 by n2 + 1
W2_grad = DZ3' * X2 / m;

% Delta2 = m by n1 + 1
Delta2 = DZ3 * W2(:, 2 : end);
DZ2 = Delta2 .* A2 .* (1 - A2);

% W2_grad = n2 by n1 + 1
W1_grad = DZ2' * X1 / m;

W2_grad(:, 2 : end) += lambda / m * W2 (:, 2 :end);
W1_grad(:, 2 : end) += lambda / m * W1 (:, 2 :end);
	
% Unroll gradients
grad = unrollParms(W1_grad, W2_grad);

end
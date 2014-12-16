function [J grad] = nnLogisticCostFunction(params, ...
                                   s2, s3, ...
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
[m, s1] = size(X);
s4 = size(Y, 2);

[W1 W2 W3] = rollParms(params, s1, s2, s3, s4);

% Setup some useful variables 
         
J = 0;

W1_grad = zeros(size(W1));
W2_grad = zeros(size(W2));
W3_grad = zeros(size(W3));

% X = m by s1
% Y = m by s4
% W1 = s2 by (s1 + 1)
% W2 = s3 by (s2 + 1)
% W3 = s4 by (s3 + 1)

% X1 m by s1 + 1

ONES = ones(m, 1);
X1 = [ ONES X ];

% Z2 = m by s2
Z2 = X1 * W1';
A2 = sigmoid(Z2);
X2 = [ONES A2];

%A3 = m by s3 + 1
Z3 = X2 * W2';
A3 = sigmoid(Z3);
X3 = [ONES A3];

%A4 = m by s4 + 1
Z4 = X3 * W3';
A4 = sigmoid(Z4);

% Delta4 = m by s4
Delta4 = A4 - Y;

J = (sum(sum( Delta4 .^2 )) + (sum(sum(W1(:, 2 : end) .^ 2)) + sum(sum(W2(:, 2 : end) .^ 2)) + + sum(sum(W3(:, 2 : end) .^ 2))) * lambda) / 2 / m;

DZ4 = Delta4 .* A4 .* (1 - A4);

% Compute error backpropagation

% Delta2 = m by s2 + 1
Delta3 = DZ4 * W3(:, 2 : end);
DZ3 = Delta3 .* A3 .* (1 - A3);

% Delta1 = m by s1 + 1
Delta2 = DZ3 * W2(:, 2 : end);
DZ2 = Delta2 .* A2 .* (1 - A2);

% Compute gradients

% W3_grad = s4 by s3 + 1
W3_grad = DZ4' * X3 / m;

% W2_grad = s3 by s3 + 1
W2_grad = DZ3' * X2 / m;

% W2_grad = s2 by s1 + 1
W1_grad = DZ2' * X1 / m;

% Regularize gradients
W1_grad(:, 2 : end) += lambda / m * W1 (:, 2 :end);
W2_grad(:, 2 : end) += lambda / m * W2 (:, 2 :end);
W3_grad(:, 2 : end) += lambda / m * W3 (:, 2 :end);
	
% Unroll gradients
grad = unrollParms(W1_grad, W2_grad, W3_grad);

end
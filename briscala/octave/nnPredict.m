function Y = nnPredict(X, W1, W2, W3)
%nnPredict Implements the neural network cost function for a two layer
%neural network which performs regression
%   [J grad] = nnPredict(X, Y, W1, W2, W3)
%   computes the output of neural network for each inputs
%

[m, s1] = size(X);
s2 = size(W1, 1);
s3 = size(W2, 1);
s4 = size(W3, 1);

ONES = ones(m, 1);

X1 = [  ONES X ];
Z2 = X1 * W1';

X2 = [ ONES  sigmoid(Z2) ];
Z3 = X2 * W2';

X3 = [ ONES  sigmoid(Z3) ];

Y = sigmoid(X3 * W3');

endfunction

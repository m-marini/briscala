function [Y1 Y2 Y3 Y4] = network(X, W1, W2, W3)
% [Y1 Y2 Y3 Y4] = neuroEval(X, W1, W2, W3)

m = size(X,1);
Y1 = X;
Y2 = sigmoid([ones(m, 1) X] * W1');
Y3 = sigmoid([ones(m, 1) Y2] * W2');
Y4 = sigmoid([ones(m, 1) Y3] * W3');

endfunction
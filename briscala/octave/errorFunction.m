function err = errorFunction(X, Y, W1, W2, W3)

m =  size(X, 1);
YP = nnPredict(X, W1, W2, W3);
err = sqrt(sum(sum((YP - Y) .^ 2)) / m );

endfunction
function err = errorFunction(X, Y, parms, noHidden)

[m, n] =  size(Y);
Yp = nnPredict(X, parms, noHidden, n);
err = sqrt(sum(sum((Yp - Y) .^ 2)) / m );

endfunction
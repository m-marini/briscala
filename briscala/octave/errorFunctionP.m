function err = errorFunctionP(X, Y, W, s2, s3)
%  err = errorFunctionP(X, Y, W, s2, s3)

[W1 W2 W3] =  rollParms(W,  size(X, 2), s2, s3, size(Y, 2));
err = errorFunction(X, Y, W1, W2, W3);
endfunction
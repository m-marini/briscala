function Z = split(X)
	idx0 = find(X( : , 1) == -1 | X( : , 2));
	idx1 = find(X( : , 1) == -1 | !X( : , 2));
	X0= X(idx0, : );
	X1 = X(idx1, : );
	X0( : , 2) = 1;
	X1( : , 2) = 0;
	Z = [ X0 ; X1 ];
endfunction
function F = actionFeatures(X)
	[m n] = size(X);
	F = zeros(m, 3);
	for i = 1 : m
		Cards = X( i, 6 : end);
		idx = actionIndex( Cards, X(i, 1) );
		if (idx > 0)
			F(i, idx) = 1;
		endif
	endfor
endfunction

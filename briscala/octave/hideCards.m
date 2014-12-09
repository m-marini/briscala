function Z = hideCards(X, trump)
	Z = X;
	if (sum(X == 6))
		Z(Z == 1) = 6;
	endif
endfunction

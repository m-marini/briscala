function F = cardFeatures(X)
	[m n] = size(X);
	Z = zeros(m, 40);
	F = zeros(m, 7 * 40) ;
	for i = 1 : m
		Cards = X( i, 6 : end);
		Z(i, : ) = hideCards(Cards, X(i, 5));
	endfor
	for i = 1 : m
		for j = 0 : 39
			F(i, 1 + j * 7 + Z(i, j + 1)) = 1;
		endfor
	endfor
endfunction
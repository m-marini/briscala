load -ascii "../briscola.mat";

#
# Filter player 0 card
#

function n = count(X, i)
	if (i < 0)
		n = 0;
	else
		n = sum( X(1 : i + 1) == X(i+1) );
	endif
endfunction

function Z = hide(X, trump)
	Z = X;
	if (sum(X == 6))
		Z(Z == 1) = 6;
	endif
endfunction

function F = cardFeatures(X)
	[m n] = size(X);
	Z = zeros(m, 40);
	F = zeros(m, 7 * 40) ;
	for i = 1 : m
		Cards = X( i, 6 : end);
		Z(i, : ) = hide(Cards, X(i, 5));
	endfor
	for i = 1 : m
		for j = 0 : 39
			F(i, 1 + j * 7 + Z(i, j + 1)) = 1;
		endfor
	endfor
endfunction

function F = actionFeatures(X)
	[m n] = size(X);
	F = zeros(m, 3);
	for i = 1 : m
		Cards = X( i, 6 : end);
		idx = count( Cards, X(i, 1) );
		if (idx > 0)
			F(i, idx) = 1;
		endif
	endfor
endfunction

function R = rewards(X)
	R = X(: ,2 : 4);
endfunction

CF = cardFeatures(briscola);

AF = actionFeatures(briscola);

R = rewards(briscola);

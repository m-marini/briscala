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

function Z = transform(X)
	[m n] = size(X);
	Z = zeros(m, 43);
	for i = 1 : m
		Cards = X( i, 5 : end);
		Z(i, 1) = count( Cards, X(i, 1) );
		Z(i, 2 : 3) = X(i, 2 : 3);
		Z(i, 4 : end) = hide(Cards, X(i, 4));
	endfor
endfunction

out = transform(briscola)

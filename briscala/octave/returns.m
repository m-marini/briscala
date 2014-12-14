function R = returns(X)
% returns implements the computation of returns in each step.
% The returns are 1 when the player will to win, -1 when he/she will loose and
% 0 when he/she will draw.

m = size(X, 1);
R = zeros(m, 1);

% find end episode indexes
Idx = find(X( : , 1) == -1);
X1 = X(Idx, 1 : 4);

% Compute the score of player turn
S = X1( : , 3);
S( X1( : ,2) == 0 ) = X1( X1(:,2) == 0, 4 );

% Compute if player win
W = S > 60;

n = size(Idx, 1);
for i = 1 :  n - 1
	R(Idx(i) : Idx(i + 1) -1) = W(i);
endfor
R( Idx(n) : end ) = W(n);

endfunction
function R = returns(X, gamma)
	m = size(X, 1);
	R = zeros(m, 1);
	a = 0;
	for i = 1 : m - 1
		if X(i,1) == -1
			a = 0;
		endif
		reward = (X(i, 3) > 60) - (X(i, 4) > 60);
		if !X(i, 2)
			reward = -reward;
		endif
		a = a * gamma + reward;
		R(i +1) = a;
	endfor
	R(X( : , 1) == -1) = 0;
endfunction
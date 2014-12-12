function Y = mobileAvg(X, np)
	[ m n ]= size(X);
	Y = zeros(m - np + 1, n);
	s = sum(X(1 : np, :), 1);
	for i = 1 : m - np
		Y(i, :) = s / np;
		s += X(i + np, :)  - X(i, :);
	endfor
	Y(end, :) = s / np;
endfunction
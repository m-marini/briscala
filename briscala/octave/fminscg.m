function [NW, J] = fminscg(W, s2, s3, X, Y, alpha, c, maxIter,ad = 1 / 3, minDiff = 1e-4)
%  [NW, J] = fminscg(W, s2, s3, X, Y, alpha, c, maxIter,ad = 1 / 3, minDiff = 1e-3)

[m, s1] = size(X);
s4 = size(Y, 2);

J = zeros(m, 1);
for j = 1 : maxIter
	avg = 0;
	for i = 1 : m
		[cost Grad] = nnLogisticCostFunction(W, s2, s3, X(i, : ), Y(i, :), c);
		W = W - alpha * Grad;
		J( (j - 1) * m + i) = cost;
		avg += cost;
	endfor
	avg /= m;
	if j > 1
		d = avg - prev;
		if (d > -minDiff * prev) && (d < 0)
			% if differential cost is lower than 1e-3 stop iteration
			break;
		endif
		if d > 0
			% if cost is increasing reduce the alpha coefficent
			alpha *= ad;
		endif
	endif
	prev = avg;
endfor

NW = W;

endfunction
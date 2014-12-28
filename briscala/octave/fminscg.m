function [NW, J, E] = fminscg(W, s2, s3, X, Y, alpha, c, maxIter, ad = sqrt(0.1), minDiff = 1e-4)
%  [NW J E] = fminscg(W, s2, s3, X, Y, alpha, c, maxIter, ad = sqrt(0.1), minDiff = 1e-3)

[m, s1] = size(X);
s4 = size(Y, 2);

J = zeros(m, 1);
E = zeros(m, 1);
for j = 1 : maxIter
	avg = 0;
	fprintf("Iteration %d\r", j);
	for i = 1 : m
		[cost Grad] = nnLogisticCostFunction(W, s2, s3, X(i, : ), Y(i, :), c);
		J( (j - 1) * m + i) = cost;
		E( (j - 1) * m + i) = errorFunctionP(X(i, : ), Y(i, :), W, s2, s3);
		avg += cost;
		W = W - alpha * Grad;
	endfor
	avg /= m;
	if j > 1
		d = avg - prev;
		if d > 0
			% if cost is increasing reduce the alpha coefficent
			alpha *= ad;
		elseif (d > -minDiff * prev) && (d < 0)
			% if differential cost is lower than 1e-3 stop iteration
			break;
		endif
	endif
	prev = avg;
	if exist('OCTAVE_VERSION')
		fflush(stdout);
	endif
endfor
fprintf("\n");
NW = W;

endfunction
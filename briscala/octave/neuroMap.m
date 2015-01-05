function neuroMap(W1,W2,W3, i=4,j=1,smin = -1, smax = 1, steps = 33)
% neuroMap(W1, W2, W3, i=4, j=1, smin = -1, smax = 1, steps = 33)
s = linspace(smin, smax, steps)';
[x y] = meshgrid(s,s);
X = [x(:) y(:)];
m = size(X,1);
[X1 X2 X3 X4] = network(X, W1, W2, W3);
if i == 1
	Z = X1( : , j);
elseif i  == 2
	Z = X2( : , j);
elseif i  == 3
	Z = X3( : , j);
elseif i  == 4
	Z = X4( : , j);
else
	Z = zeros(m, 1);
endif
tz = reshape(Z, steps, steps);
mesh(s, s, tz);

endfunction

function plotMap(f, smin = -1, smax = 1, steps = 33)
% plotMap(f, smin = -1, smax = 1, steps = 33)

s = linspace(smin, smax, steps)';
[x y] = meshgrid(s,s);
X = [x(:) y(:)];
m = size(X,1);
Z = f(X);
tz = reshape(Z, steps, steps);
mesh(s, s, tz);

endfunction
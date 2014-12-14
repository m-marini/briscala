function Y = removeFinal(X)
% removeFinal implements the remove of last two move of each episode
% the last two move are unusefull moves because the players have no choice

[m n] = size(X);

% find end state indexes
EE = find(X( : , 1) == -1);

mask = zeros(m, 1);
mask(EE+1) = 1;
mask(EE+2) = 1;

Y = X(!mask, :);

endfunction
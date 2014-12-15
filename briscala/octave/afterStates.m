function AS = afterStates(S)
% afterStates filter the after states
%

%remove last state because it is an initial state
S = S(1 : end - 1, :);

% the initial state of an episode is not an afterstate so remove all of them from set
% find end episodes

EE = find(S( : , 1) == -1);

%compute the initial state indexes
IE = EE - 1;
IE = IE(IE > 0);

% filter the afterstates
F = ones(size(S, 1),1);
F(IE) = 0;
AS = S(F != 0, : );

endfunction
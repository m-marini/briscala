function AS = afterStates(S)
% afterStates filter the after states.
% All initial states of  episode are not an afterstates
%
% AS = afterStates(S)
%  S: the states

%remove last state because it is an initial state
S = S(1 : end - 1, :);

% find end episodes
EE = find(S( : , 1) == -1);

% The previous index of an end state is an initial state index
IE = EE - 1;
IE = IE(IE > 0);

% filter the afterstates
F = ones(size(S, 1),1);
F(IE) = 0;
AS = S(F != 0, : );

endfunction
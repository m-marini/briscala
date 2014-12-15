function Z = splitPlayers(X)
% split implements the split of episode step between the player 0 afterstates
% and the player 1 afterstates.

% !!!!! CHECK FOR END STATE DUPLICATION !!!!!

% the afterstates index of player 0 is the final state plus the state of player 1 turn
idx0 = find(X( : , 1) == -1 | !X( : , 2));

% the afterstates index of player 1 is the final state plus the state of player 0 turn
idx1 = find(X( : , 1) == -1 | X( : , 2));

X0= X(idx0, : );
X1 = X(idx1, : );

X0( : , 2) = 1;
X1( : , 2) = 0;

Z = [ X0 ; X1 ];

endfunction
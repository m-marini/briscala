function [X Y] = toFeatures(S)
% toFeatures convert the step data into features
%  S are the step data
% gamma is the discount rate of rewards
%

% remove final
S = removeFinal(S);

% filter after states
S = afterStates(S);

% Split the episode regarding player 0 and player 1 perspectives
S = splitPlayers(S);

% compute the card features
X = cardFeatures(S); 

% compute the return values
Y = returns(S);

% Compute state value
[X Y] = stateValues(X, Y);

endfunction
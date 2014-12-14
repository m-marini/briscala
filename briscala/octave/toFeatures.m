function [X Y] = toFeatures(S)
% toFeatures convert the step data into features
%  S are the step data
% gamma is the discount rate of rewards
%

% Split the episode regarding player 0 and player 1 perspectives
S = split(afterStates(S));

% compute the card features
X = cardFeatures(S); 

% compute the return values
Y = returns(S);

endfunction
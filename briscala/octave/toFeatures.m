function [X Y] = toFeatures(S, gamma)
% toFeatures convert the step data into features
%  S are the step data
% gamma is the discount rate of rewards
%

% Split the episode regarding player 0 and player 1 perspectives
S = split(S);

% compute the card features
CF = cardFeatures(S); 

% compute the return values
R = returns(S, gamma);

% the first state is not an afterstate so remove all of them from set

% Find the end episode step and mark previous ones
EE = S( : , 1) != -1;

X =[AF CF](find(EE), : );

Y = R(find(EE), : );

endfunction
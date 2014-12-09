function [X Y] = convert(S, gamma)

S = split(S);

AF = actionFeatures(S);
CF = cardFeatures(S); 
R = returns(S, gamma);
EE = S( : , 1) != -1;

X =[AF CF](find(EE), : );

Y = R(find(EE), : );

endfunction
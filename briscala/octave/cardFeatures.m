function F = cardFeatures(X)
% cardFeatures implements the tranformations of card states into crad features

[m n] = size(X);
Z = zeros(m, 40);
F = zeros(m, 7 * 10 + 6 * 30) ;

% Extract card states
InitCards = X( : , 6 : end);

% Revers the status of cards when turn of player 1
Cards = InitCards;
p1Mask = X( : , 2) == 0;
Cards(InitCards == 0 & p1Mask)=1;
Cards(InitCards == 1 & p1Mask)=0;
Cards(InitCards == 2 & p1Mask)=3;
Cards(InitCards == 3 & p1Mask)=2;

% The card of opposite player have to be hidden
% The trump state does not need to be hidden

% Count the card on deck
DeckMask = sum(Cards == 6, 2)>0;

% hide the status of opposite when there is at least a deck card

Cards(DeckMask & Cards== 1) = 6;

map = [0 1 2 3 4 6 5];
Cards = map(Cards + 1);

% Map the state to the features
for i = 1 : m
	for j = 0 : 9
		F(i,  Cards(i, j + 1) + j * 7 + 1) = 1;
	endfor
	for j = 0 : 29
		F(i,  Cards(i, j + 11) + j * 6 + 71) = 1;
	endfor
endfor

endfunction
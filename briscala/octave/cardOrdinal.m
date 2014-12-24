function n = cardOrdinal(Cards, i)
% cardOrdinal returns the ordinal of a card
%
%  n = cardOrdinal(Cards, i)
%   Cards: the state of the cards
%   i: the selected card  index
%
%   n:  the ordinal of card

if (i < 0)
	n = 0;
else
	n = sum( Cards(1 : i + 1) == Cards(i + 1) );
endif

endfunction

load -ascii "../briscola.mat";

briscola = briscola(1 : 43, : );
indexes0 = briscola( : , 2) == 1;
indexes1 = briscola( : , 2) == 0;

#
# Filter player 0 card
#

function z = transform0(x)
	cards = x( : , 5 : end);
	cards = 
	z = cards;
endfunction

transform0(briscola(indexes0, :))

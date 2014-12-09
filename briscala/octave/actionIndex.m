function n = actionIndex(X, i)
	if (i < 0)
		n = 0;
	else
		n = sum( X(1 : i + 1) == X(i+1) );
	endif
endfunction

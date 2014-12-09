function R = reward(X)
	idx0 =X( : , 2);
	idx1 =!X( : , 2);
	R = (X( : , 3) > 60) - (X(:, 4) > 60);
	R(idx1) = -R(idx1) 
endfunction

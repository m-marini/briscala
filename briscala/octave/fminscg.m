function [new_parms, J] = fminscg(parms, n2, x, y, lambda, alpha)
	[J grad] = nnCostFunction(parms, n2, x, y, lambda);
	new_parms = parms - alpha * grad;
end
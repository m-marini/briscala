function parms = unrollParms(W1, W2)
%unrollParms Implements the function for unrolling
%neural network parameters to a vector
%   parms = unrollParms(W1, W2)
%  The network parameters W1 , W2 are "unrolled" to the vector
%

parms = [W1(:); W2(:)];

endfunction

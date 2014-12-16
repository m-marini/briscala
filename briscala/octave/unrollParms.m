function parms = unrollParms(W1, W2, W3)
%unrollParms Implements the function for unrolling
%neural network parameters to a vector
%   parms = unrollParms(W1, W2, W3)
%  The network parameters W1, W2, W3are "unrolled" to the vector
%

parms = [ W1(:); W2(:); W3(:) ];

endfunction

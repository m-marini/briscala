function X = toFeatures(Indexes, n)
% toFeatures reads the filename and returns the value and actions  function values
%  X = toFeatures(Indexes, n)
%   Indexes: the index of features
%   n: the number of features
% 
%  X: are the features

m = size(Indexes, 1);
X = zeros(m, n);
for i = 1 : m
	X( i , Indexes(i, : )) = 1;
endfor

endfunction
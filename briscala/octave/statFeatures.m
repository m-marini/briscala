function [NX, NY] = statFeatures(X, Y)
% statFeatures implement the transformation from a feature set of episode
% to state values.

[m  n] = size(X, 1);
XY = [ X Y ];
ni = 41;
IXY = zeros(m, ni);

for i = 1 : m
	IDX = find(XY(i , :) == 1);
	p = ni - length(IDX);
	IDX = [ IDX zeros(1, p) ];
	IXY(i, :) = IDX;
endfor

IXY = sortrows(IXY);
NXY = zeros(m, n + 1);
for i = 1 : m
	IDX = IXY(find(IXY(i, :) > 0)); 
	NXY(m, IDX) = 1;
endfor
endfunction
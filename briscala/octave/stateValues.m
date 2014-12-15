function [NX, NY] = stateValues(X, Y)
% statFeatures implement the transformation from a feature set of episode
% to state values.

[m  n] = size(X);
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
CX = zeros(m, n);
CY = zeros(m, 1);
for i = 1 : m
	IDX = IXY(i,:);
	IDX = IDX(find(IDX>0));
	CXY = zeros(1, n + 1);
	CXY(IDX) = 1;
	CX(i, :) = CXY(1 : n);
	CY(i) = CXY(end);
endfor

NX=zeros(1, n);
NY = zeros(1, 1);
idx = 1;
c0 = 0;
c1 = 0;
CS = CX(1, : );
for i = 1 : m
	if any(CX(i, : ) != CS)
		NX(idx, : ) = CS;
		NY(idx, : ) = c1 / (c0 + c1);
		c0 = 0;
		c1 = 0;
		CS = CX(i, : );
		++idx;
	endif
	if CY(i) == 1
		++c1;
	else
		++c0;
	endif
endfor
NX(idx, : ) = CS;
NY(idx, : ) = c1 / (c0 + c1);

endfunction
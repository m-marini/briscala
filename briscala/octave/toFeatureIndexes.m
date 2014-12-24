function [VX VY, QX, QY] = toFeatureIndexes(filename="../briscola-10.mat")
% toFeatureIndexes reads the filename and returns the value and actions  function values
%  [VX VY, QX, QY] = toFeatureIndexes(filename="../briscola-10.mat")
%   filename: is the filename to read
% 
%  VX: are the state features
%  VY: are the state values
%  QX: are the action features
%  QY: are the action values

load(filename);
 
 VY = V( : , 41) ./ V( : , 42);
QY = Q( : , 42) ./ Q( : , 43);

vm = size(V, 1);
qm = size(Q, 1);

VX = zeros(vm, 40);
QX = zeros(qm, 41);

% The trumps can have 6 state (Player, Won, Lost, Played, Deck, Trump)

for i = 1 : 10
	VX( : , i) =  V( : , i) + ( i * 6 - 5);
	QX( : , i) =  Q( : , i) + ( i * 6 - 5);
endfor

for i = 11 : 40
	VX(:, i) =  V(: , i) + ( i * 5 + 6);
	QX(:, i) =  Q(: , i) + ( i * 5 + 6);
endfor

for i = 1 : qm
	QX(i, 41) =  cardOrdinal(Q(i, 1 : 40), Q(i, 41)) + 210;
endfor

endfunction
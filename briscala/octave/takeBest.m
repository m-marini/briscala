function takeBest(pattern)
% function takeBest(pattern)

Files = readdir(".");
Files = Files( find( fnmatch( pattern, Files) ) );

m = size(Files, 1);
if m > 1
	file = Files{1};
	load(file, "validationError");
	best = validationError;
	idx = 1;
	for i = 2 : m
		load(Files{i}, "validationError");
		if (validationError<best)
			best = validationError;
			idx = i;
		endif
	endfor
	for i = 1 : m
		if i != idx
			printf("Coping %s to %s\n", Files{idx}, Files{i});
				copyfile(Files{idx}, Files{i}, "f");
		endif
	endfor
endif

endfunction
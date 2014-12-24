#!/bin/bash
octave --eval 'takeBest("best-*.mat")';
for i in $(eval echo {1..$1})
do
    gnome-terminal -x octave --persist --eval 'main("best-'$i'.mat")'
done


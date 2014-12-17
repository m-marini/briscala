#!/bin/bash
for i in $(eval echo {1..$1})
do
    gnome-terminal -x octave --persist --eval 'main("best-'$i'.mat")'
done


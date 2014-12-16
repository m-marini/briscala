#!/bin/bash
for i in $(eval echo {1..$1})
do
    gnome-terminal -x octave --persist --eval 'main("file'$i'.mat")'
done


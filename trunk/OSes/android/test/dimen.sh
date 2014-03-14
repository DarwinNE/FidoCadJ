#!/bin/bash

#Author: Dante Loi

#This script makes the layouts for each screen size, 
#it need a layout made for the normal screen size and
#a dimension file that contains the dimension resources.
#I would merge this script with res.sh, now the output of 
#dimen.sh is just a proposal for the resources' architecture.

if test "$#" -ne "1"
then 
	echo "Usage:"
	echo "      ./dimen.sh [LAYOUT FILE NAME]"
	exit -1
fi

for size in "small" "large" "xlarge" 
do
	if test -d "res/res/layout-$size"
	then 
		mkdir res/layout-$size
		touch res/layout-$size/$1
	fi

	`sed s/normal/$size/ < res/layout/$1 > res/layout-$size/$1` 
done

exit 0


#!/bin/bash

#Author: Dante Loi

#portable implementation of readlink -f
function absolute
{
	local TARGET_FILE=$1

	cd `dirname $TARGET_FILE`
	TARGET_FILE=`basename $TARGET_FILE`

	while test -L "$TARGET_FILE"
	do
 	    TARGET_FILE=`readlink $TARGET_FILE`
    	    cd `dirname $TARGET_FILE`
    	    TARGET_FILE=`basename $TARGET_FILE`
	done

	local PHYS_DIR=`pwd -P`

	echo $PHYS_DIR/$TARGET_FILE
}


if test "$#" -ne "1"
then 
	echo "Usage:"
	echo "      ./res.sh [PROPERTIES FILES PATH]"
	exit -1
fi

if test ! -d res 
then
	mkdir res
	mkdir res/values
fi

for file in `ls $1 | grep .*properties`
do
	language=${file:15:2}
	file=$(absolute $1/$file)
	
	if test $language = "en"  
	then 
		`./proTOxml.py $file "res/values/strings.xml"`
		
	elif test ! -d res/values-$language
	then 
		mkdir res/values-$language
		`./proTOxml.py $file "res/values-$language/strings.xml"`
	
	else 
		`./proTOxml.py $file "res/values-$language/strings.xml"`
	fi
done

exit 0

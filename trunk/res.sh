#!/bin/bash

#Dante Loi 25/02/2014

if test ! -e res 
then
	mkdir res
	mkdir res/values
fi

properties=`ls bin | grep ".*properties"`
language=""

for file in $properties
do
	language=${file:15:2}
	
	if test $language = "en"  
	then 
		`./proTOxml.py "bin/$file" "res/values/strings.xml"`
		
	elif test ! -e res/values-$language
	then 
		mkdir res/values-$language
		`./proTOxml.py "bin/$file" "res/values-$language/strings.xml"`
	
	else 
		`./proTOxml.py "bin/$file" "res/values-$language/strings.xml"`
	fi
done

exit 1

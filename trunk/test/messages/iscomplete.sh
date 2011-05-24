#!/bin/sh

echo "Testing $1 using $2 as a reference"

# At first, we extract the keys of each resource

awk -F = '1{print $1}' $1 >language_temp_test.txt
awk -F = '1{print $1}' $2 >language_temp_reference.txt

# Then, we test each line against the reference.
# this is not very fast, but it is OK for a moderate
# amount of keys.

isok=1
for t in $(cat language_temp_reference.txt) 
do
	if grep $t language_temp_test.txt >/dev/null;
	then
		rrr=1	
	else
		echo "Test failed: $t"
		isok=0 
	fi
	
done
if [ $isok -eq 1 ]; then
	echo "Test passed"
fi
	
# Cleanup!

rm language_temp_test.txt
rm language_temp_reference.txt


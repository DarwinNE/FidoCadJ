#!/bin/sh

# At first, we extract the keys of each resource

awk -F = '1{print $1}' $1 >language_temp_test.txt
awk -F = '1{print $1}' $2 >language_temp_reference.txt

sed -ie 's/ //' language_temp_reference.txt 

# Then, we test each line against the reference.
# this is not very fast, but it is OK for a moderate
# amount of keys.

test_failed=0
for t in $(cat language_temp_reference.txt) 
do
    if grep $t language_temp_test.txt >/dev/null;
    then
        rrr=1   
    else
        if test $test_failed == 0
        then
            echo "Test $1 against $2 "
        fi
        printf "\033[1mTest failed: '$t'\033[0m\n"
        test_failed=1
    fi
done
if [ $test_failed -eq 0 ]; then
    echo "Test $1 against $2: OK "
fi
# Cleanup!

rm language_temp_test.txt
rm language_temp_reference.txt
exit $test_failed

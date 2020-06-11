#!/bin/sh

echo "Testing the completeness of all installed languages"
echo "---------------------------------------------------"
echo ""

cd ../../bin

reference=MessagesBundle_en.properties

echo "The reference file will be " $reference
test_failed=0

for language in $(ls *.properties)
do
    ../test/messages/iscomplete.sh $language $reference
    if test $? != 0
    then
        test_failed=1
    fi
done
echo ""
exit $test_failed

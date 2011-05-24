#!/bin/sh

echo "Testing the completeness of all installed languages"
echo "---------------------------------------------------"
echo ""

cd ../../bin

reference=MessagesBundle_it.properties

echo "The reference file will be " $reference

for language in $(ls *.properties)
do
	echo $language
	../test/messages/iscomplete.sh $language $reference
done

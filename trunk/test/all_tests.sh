#!/bin/sh

echo ""
printf "\033[1m   F i d o C a d J\n"
echo ""
echo "   Automatic test suite"
echo "   by Davide Bucci 2011-2012"
printf "\033[0m\n"
echo ""

test_failed=0

cd export
./test_export.sh

if test $? != 0
then
	test_failed=1
fi


cd ../messages
./test_messages.sh

if test $? != 0
then
        test_failed=1
fi


cd ../size
./test_size.sh

if test $? != 0
then
        test_failed=1
fi

cd ..


echo
echo "Final report"
echo "------------"

if test $test_failed != 0
then
	printf "\033[1m----> Some test failed!\033[0m\n"
else
	echo "All tests were successful!"
fi




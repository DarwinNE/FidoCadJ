#!/bin/sh

echo ""
printf "\033[1m   F i d o C a d J\n"
echo ""
echo "   Automatic test suite"
echo "   by Davide Bucci 2011-2015"
printf "\033[0m\n"
echo ""

printf " NOTE: the following tests will run ../jar/fidocadj.jar\n"
printf "       Make sure you updated this file with the createjar.sh script\n\n"
test_failed=0

if [ ! -f ../jar/fidocadj.jar ];
then
   echo "\033[1m\x1b[31m"
   echo "../jar/fidocadj.jar does not exist! You should compile FidoCadJ and"
   echo "pack it into the fidocadj.jar file to run the test. Employ the compile"
   echo "and createjar scripts, as described in the README.md file. \033[0m"
   exit 1
fi

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
    printf "\033[1m\x1b[31m----> Some test failed!\033[0m\n"
else
    printf "\x1b[32mAll is Ok! :-)\033[0m\n" 
fi

#!/bin/sh

echo ""
echo -e "\033[1m   F i d o C a d J"
echo ""
echo "   Automatic test suite"
echo "   by Davide Bucci 2011-2012"
echo -e "\033[0m"
echo ""

cd export
./test_export.sh

cd ../messages
./test_messages.sh

cd ../size
./test_size.sh

cd ..


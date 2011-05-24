#!/bin/sh

echo ""
echo "   F i d o C a d J"
echo ""
echo "   Automatic test suite"
echo "   by Davide Bucci 2011"
echo ""
echo ""

cd export
./test_export.sh

cd ../messages
./test_messages.sh


#!/bin/sh

echo "Calculating the size of the various primitives."
if test $# != 1
then
	echo "Usage: $0 {directory}"
	exit 1
fi

echo "Output in $1/"

printf "  Bezier...     "
java -jar ../../jar/fidocadj.jar -n -s primitives/bezier.fcd >$1/bezier.txt
printf "Done\n  Connection... "
java -jar ../../jar/fidocadj.jar -n -s primitives/connection.fcd >$1/connection.txt
printf "Done\n  Curve...      "
java -jar ../../jar/fidocadj.jar -n -s primitives/curve.fcd >$1/curve.txt
printf "Done\n  Ellipse...    "
java -jar ../../jar/fidocadj.jar -n -s primitives/ellipse.fcd >$1/ellipse.txt
printf "Done\n  Line...       "
java -jar ../../jar/fidocadj.jar -n -s primitives/line.fcd >$1/line.txt
printf "Done\n  PCB line...   "
java -jar ../../jar/fidocadj.jar -n -s primitives/pcbline.fcd >$1/pcbline.txt
printf "Done\n  PCB pad...    "
java -jar ../../jar/fidocadj.jar -n -s primitives/pcbpad.fcd >$1/pcbpad.txt
printf "Done\n  Polygon...    "
java -jar ../../jar/fidocadj.jar -n -s primitives/polygon.fcd >$1/polygon.txt
printf "Done\n  Rectangle...  "
java -jar ../../jar/fidocadj.jar -n -s primitives/rectangle.fcd >$1/rectangle.txt
printf "Done\n  Text...       "
java -jar ../../jar/fidocadj.jar -n -s primitives/text.fcd >$1/text.txt
printf "Done\n  Complex 1...  "
java -jar ../../jar/fidocadj.jar -n -s complex/source1.fcd >$1/complex1.txt
printf "Done\n  Complex 2...  "
java -jar ../../jar/fidocadj.jar -n -s complex/source2.fcd >$1/complex2.txt
printf "Done\n"

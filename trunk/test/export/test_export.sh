#!/bin/sh

echo "Test script for graphical export facility of FidoCadJ"
echo

echo "Testing the export on bitmap formats"
echo PNG
java -jar ../../jar/fidocadj.jar -n -c r2 png png/test_out.png original/test_pattern.fcd
echo JPG
java -jar ../../jar/fidocadj.jar -n -c r2 jpg jpg/test_out.jpg original/test_pattern.fcd
echo

echo Testing the export on vector formats
echo "SVG (Scalar Vector Graphic)"
java -jar ../../jar/fidocadj.jar -n -c r2 svg svg/test_out.svg original/test_pattern.fcd
echo "EPS (Encapsulated Postscript)"
java -jar ../../jar/fidocadj.jar -n -c r2 eps eps/test_out.eps original/test_pattern.fcd
echo "PDF (Portable Document Format)"
java -jar ../../jar/fidocadj.jar -n -c r2 pdf pdf/test_out.pdf original/test_pattern.fcd
echo "SCR (script for CadSoft Eagle)"
java -jar ../../jar/fidocadj.jar -n -c r2 scr scr/test_out.scr original/test_pattern.fcd
echo "PGF (PGF/TikZ script for LaTeX)"
java -jar ../../jar/fidocadj.jar -n -c r2 pgf pgf/test_out.pgf original/test_pattern.fcd

echo "The reference drawing has been exported in all the supported formats. You might check the results now..."

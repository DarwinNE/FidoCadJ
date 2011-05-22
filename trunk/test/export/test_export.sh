#!/bin/sh

# All the tests are being run in the headless mode. This avoid some kind of appearing and
# disappearing icon in the dock and it makes Java startup a little faster.

echo "Test script for graphical export facility of FidoCadJ"
echo

echo "Testing the export on bitmap formats"
echo "PNG (Portable Network Graphic)"
java -Djava.awt.headless=true -jar ../../jar/fidocadj.jar -n -c r2 png png/test_out.png original/test_pattern.fcd
echo "JPG (Joint Photographic Experts Group)" 
java -Djava.awt.headless=true -jar ../../jar/fidocadj.jar -n -c r2 jpg jpg/test_out.jpg original/test_pattern.fcd
echo

echo Testing the export on vector formats
echo "SVG (Scalar Vector Graphic)"
java -Djava.awt.headless=true -jar ../../jar/fidocadj.jar -n -c r2 svg svg/test_out.svg original/test_pattern.fcd
echo "EPS (Encapsulated Postscript)"
java -Djava.awt.headless=true -jar ../../jar/fidocadj.jar -n -c r2 eps eps/test_out.eps original/test_pattern.fcd
echo "PDF (Portable Document Format)"
java -Djava.awt.headless=true -jar ../../jar/fidocadj.jar -n -c r2 pdf pdf/test_out.pdf original/test_pattern.fcd
echo "SCR (script for CadSoft Eagle)"
java -Djava.awt.headless=true -jar ../../jar/fidocadj.jar -n -c r2 scr scr/test_out.scr original/test_pattern.fcd
echo "PGF (PGF/TikZ script for LaTeX)"
java -Djava.awt.headless=true -jar ../../jar/fidocadj.jar -n -c r1.5 pgf pgf/test_out.pgf original/test_pattern.fcd
cd pgf
pdflatex test_out.tex
rm test_out.aux
rm test_out.log
cd ..
echo "LaTeX test file for PGF export compiled. Please note that it is perfectly normal that the PGF export does not include the text size and font attributes, since the idea is that the user might control those aspects via LaTeX commands."

echo "The reference drawing has been exported in all the supported formats. You might check the results now..."

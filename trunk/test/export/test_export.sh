#!/bin/sh

# All the tests are being run in the headless mode. This avoid some kind of appearing and
# disappearing icon in the dock and it makes Java startup a little faster.

echo "Test script for graphical export facility of FidoCadJ"
echo "-----------------------------------------------------"
echo ""

echo "Export to bitmap formats"
echo "  PNG (Portable Network Graphic)"
java -jar ../../jar/fidocadj.jar -n -c r2 png png/test_out.png original/test_pattern.fcd >output_png_r.txt
java -jar ../../jar/fidocadj.jar -n -c 800 600 png png/test_out_r.png original/test_pattern.fcd >output_png.txt
echo "  JPG (Joint Photographic Experts Group)" 
java -jar ../../jar/fidocadj.jar -n -c r2 jpg jpg/test_out.jpg original/test_pattern.fcd >output_jpg_r.txt
java -jar ../../jar/fidocadj.jar -n -c 800 600 jpg jpg/test_out_r.jpg original/test_pattern.fcd >output_jpg.txt

echo

echo Export to vector formats
echo "  SVG (Scalar Vector Graphic)"
java -jar ../../jar/fidocadj.jar -n -c r2 svg svg/test_out.svg original/test_pattern.fcd >output_svg_r.txt
java -jar ../../jar/fidocadj.jar -n -c 800 600  svg svg/test_out_r.svg original/test_pattern.fcd >output_svg_1.txt
java -jar ../../jar/fidocadj.jar -n -c 800 600  svg svg/led_circuit_r.svg original/led_circuit.fcd >output_svg_2.txt

echo "  EPS (Encapsulated Postscript)"
java -jar ../../jar/fidocadj.jar -n -c r2 eps eps/test_out.eps original/test_pattern.fcd >output_eps.txt
echo "  PDF (Portable Document Format)"
java -jar ../../jar/fidocadj.jar -n -c r2 pdf pdf/test_out.pdf original/test_pattern.fcd >output_pdf.txt
echo "  SCR (script for CadSoft Eagle)"
java -jar ../../jar/fidocadj.jar -n -c r2 scr scr/test_out.scr original/test_pattern.fcd >output_scr.txt
echo "  PGF (PGF/TikZ script for LaTeX)"
java -jar ../../jar/fidocadj.jar -n -c r1.5 pgf pgf/test_out.pgf original/test_pattern.fcd >output_pgf.txt
cd pgf
pdflatex test_out.tex >output_latex.tex
rm test_out.aux
rm test_out.log
cd ..
echo ""
echo "LaTeX test file for PGF export compiled."
echo

./check.sh
exit $?

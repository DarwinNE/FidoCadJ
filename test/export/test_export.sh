#!/bin/sh

# All the tests are being run in the headless mode. This avoid some kind of 
# appearing and
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
java -jar ../../jar/fidocadj.jar -n -m -c 800 600 svg svg/test_exp/test.svg svg/test_exp/test.fcd >output_svg_3.txt

echo "  EPS (Encapsulated Postscript)"
java -jar ../../jar/fidocadj.jar -n -c r2 eps eps/test_out.eps original/test_pattern.fcd >output_eps.txt
echo "  PDF (Portable Document Format)"
java -jar ../../jar/fidocadj.jar -n -c r2 pdf pdf/test_out.pdf original/test_pattern.fcd >output_pdf.txt
java -jar ../../jar/fidocadj.jar -n -c r10 pdf pdf/test_out10.pdf original/test_pattern.fcd >output_pdf10.txt
echo "  SCR (script for CadSoft Eagle)"
java -jar ../../jar/fidocadj.jar -n -c r2 scr scr/test_out.scr original/test_pattern.fcd >output_scr.txt
echo "  PGF (PGF/TikZ script for LaTeX)"
java -jar ../../jar/fidocadj.jar -n -c r1 pgf pgf/test_out.pgf original/test_pattern.fcd >output_pgf.txt
cd pgf
pdflatex test_out.tex >output_latex.tex
rm test_out.aux
rm test_out.log
cd ..
echo ""
echo "LaTeX test file for PGF export compiled."
echo

echo "Check sanity of the command line inputs."
# In the first test, the extension is wrong and the file should not be
# created.
java -jar ../../jar/fidocadj.jar -n -c r2 png this_file_should_not_be_created.pdf original/test_pattern.fcd >output_sanity1.txt 2>expected_error.txt

# In the second test, the extension is wrong but the -f option is provided.
# The exported file should be created anyway, even with the wrong extension.
java -jar ../../jar/fidocadj.jar -n -f -c r2 png this_file_should_have_wrong_extension.pdf original/test_pattern.fcd >output_sanity2.txt 


./check.sh
exit $?

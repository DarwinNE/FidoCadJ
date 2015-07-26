#/bin/sh

# In the case of error, the ANSI control codes should make sort
# that the error message is well visible 

test_fail=0

echo "Checking that all the exported files are conformal to the models."
echo
echo "Now testing:"
echo "-------------"
if diff -I "CreationDate:\|Creator:" eps/test_out.eps eps/ref/test_out.eps >results_eps.txt
then
  echo "eps     OK"
  rm results_eps.txt output_eps.txt
else
  test_fail=1
  printf "\033[1meps export is not conformal to the model. Please check results_eps.txt\033[0m\n"
fi

# The 0000 codes appear in the cross-referenc table, 584 appears in after the xrefpos line. 
# This might be improved, but works for the test file.
if diff -I "/Creator\|0000\|584" pdf/test_out.pdf pdf/ref/test_out.pdf >results_pdf.txt
then
  echo "pdf     OK"
  rm results_pdf.txt output_pdf.txt
else
  test_fail=1
  printf "\033[1mpdf export is not conformal to the model. Please check results_pdf.txt\033[0m\n"
fi

if diff -I " Created by FidoCadJ" pgf/test_out.pgf pgf/ref/test_out.pgf >results_pgf.txt
then
  echo "pgf     OK"
  rm results_pgf.txt output_pgf.txt
else
  test_fail=1
  printf "\033[1mpgf export is not conformal to the model. Please check results_pgf.txt\033[0m\n"
fi

if diff -I "# Created by" scr/test_out.scr scr/ref/test_out.scr >results_scr.txt
then
  echo "scr     OK"
  rm results_scr.txt output_scr.txt
else
  test_fail=1
  printf "\033[1mscr export is not conformal to the model. Please check results_scr.txt\033[0m\n"
fi

if diff -I "Created by FidoCadJ ver." svg/test_out.svg svg/ref/test_out.svg >results_svg_s.txt
then
  echo "svg S   OK"
  rm results_svg_s.txt output_svg_1.txt output_svg_2.txt
else
  test_fail=1
  printf "\033[1msvg export based on size is not conformal to the model. Please check results_svg_s.txt\033[0m\n"
fi
if diff -I "Created by FidoCadJ ver." svg/test_out_r.svg svg/ref/test_out_r.svg >results_svg_r.txt
then
  echo "svg R   OK"
  rm results_svg_r.txt output_svg_r.txt
else
  test_fail=1
  printf "\033[1msvg export based on resolution is not conformal to the model. Please check results_svg_r.txt\033[0m\n"
fi


if diff png/test_out.png png/ref/test_out.png >results_png_r.txt
then
  echo "png R   OK"
  rm results_png_r.txt output_png_r.txt
else
  test_fail=1
  printf "\033[1mpng export based on resolution is not conformal to the model. Please check results_png_r.txt\033[0m\n"
fi
if diff png/test_out_r.png png/ref/test_out_r.png >results_png_s.txt
then
  echo "png S   OK" 
  rm results_png_s.txt output_png.txt
else
  test_fail=1
  printf "\033[1mpng export based on size is not conformal to the model. Please check results_png_s.txt\033[0m\n"
fi

if diff jpg/test_out.jpg jpg/ref/test_out.jpg
then
  echo "jpg S   OK"
  rm output_jpg.txt
else
  test_fail=1
  printf "\033[1mjpg export based on resolution is not conformal to the model. Please check\033[0m\n"
fi

if diff jpg/test_out_r.jpg jpg/ref/test_out_R.jpg
then
  echo "jpg R   OK"
  rm output_jpg_r.txt
else
  test_fail=1
  printf "\033[1mjpg export based on size is not conformal to the model. Please check\033[0m\n"
fi

if [ ! -f this_file_should_not_be_created.pdf ]
then
  echo "Sanity1 OK"
  rm output_sanity1.txt expected_error.txt
else
  test_fail=1
  printf "\033[1mSanity of CLI export checks is not OK. Verify code associated to -f option.\033[0m\n"
fi

if [ -f this_file_should_have_wrong_extension.pdf ]
then
  echo "Sanity2 OK"
  rm output_sanity2.txt this_file_should_have_wrong_extension.pdf
else
  test_fail=1
  printf "\033[1mSanity of CLI export checks is not OK. Verify code associated to -f option.\033[0m\n"
fi



if test $test_fail != 0
then
  printf "\n\033[1mWARNING: failing this test just means that the output file is not byte to byte conformal to the provided model. However, the output files could be perfectly acceptable, or they might be even better than the model. This typically happens when the version number of FidoCadJ has changed and in some cases this will generate a false positive. When those tests are failed, you should check carefully that the file contained in each subdirectory is valid. In this case, running export/update_ref.sh will update the models to the files just calculated.\033[0m\n"
fi
echo ""
exit $test_fail

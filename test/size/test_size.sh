#/bin/sh

echo "Test the calculation of image size for the various primitives"
echo "-------------------------------------------------------------"
echo

./create_size.sh .

test_failed=0

echo "Now checking the results:"
# Maybe creating a function would be better?
if diff bezier.txt references/bezier.txt >bezier_r.txt
then 	
  echo "  Bezier:     OK"
  rm bezier.txt bezier_r.txt
else
  printf "\033[1mTest failed: bezier size calculation\033[0m\n"
  test_failed=1
fi

if diff curve.txt references/curve.txt >curve_r.txt
then 	
  echo "  Curve:      OK"
  rm curve.txt curve_r.txt
else
  printf "\033[1mTest failed: curve size calculation\033[0m\n"
  test_failed=1
fi

if diff line.txt references/line.txt >line_r.txt
then 	
  echo "  Line:       OK"
  rm line.txt line_r.txt
else
  printf "\033[1mTest failed: line size calculation\033[0m\n"
  test_failed=1
fi

if diff pcbpad.txt references/pcbpad.txt >pcbpad_r.txt
then 	
  echo "  Pcb pad:    OK"
  rm pcbpad.txt pcbpad_r.txt
else
  printf "\033[1mTest failed: pcbpad size calculation\033[0m\n"
  test_failed=1
fi

if diff rectangle.txt references/rectangle.txt >rectangle_r.txt
then 	
  echo "  Rectangle:  OK"
  rm rectangle.txt rectangle_r.txt
else
  printf "\033[1mTest failed: rectangle size calculation\033[0m\n"
  test_failed=1
fi

if diff connection.txt references/connection.txt >connection_r.txt
then
  echo "  Connection: OK"
  rm connection.txt connection_r.txt
else
  printf "\033[1mTest failed: connection size calculation\033[0m\n"
  test_failed=1
fi

if diff ellipse.txt references/ellipse.txt >ellipse_r.txt
then 	
  echo "  Ellipse:    OK"
  rm ellipse.txt ellipse_r.txt
else
  printf "\033[1mTest failed: ellipse size calculation\033[0m\n"
  test_failed=1
fi

if diff pcbline.txt references/pcbline.txt >pcbline_r.txt 
then 	
  echo "  Pcb line:   OK"
  rm pcbline.txt pcbline_r.txt
else
  printf "\033[1mTest failed: pcbline size calculation\033[0m\n"
  test_failed=1
fi

if diff polygon.txt references/polygon.txt >polygon_r.txt
then 	
  echo "  Polygon:    OK"
  rm polygon.txt polygon_r.txt
else
  printf "\033[1mTest failed: polygon size calculation\033[0m\n"
  test_failed=1
fi

if diff text.txt references/text.txt >text_r.txt
then 	
  echo "  Text:       OK"
  rm text.txt text_r.txt
else
  printf "\033[1mTest failed: text size calculation\033[0m\n"
  test_failed=1
fi

if diff complex1.txt references/complex1.txt >complex1_r.txt
then 	
  echo "  Complex 1:  OK"
  rm complex1.txt complex1_r.txt
else
  printf "\033[1mTest failed: complex 1 size calculation\033[0m\n"
  test_failed=1
fi

if diff complex2.txt references/complex2.txt >complex2_r.txt
then 	
  echo "  Complex 2:  OK"
  rm complex2.txt complex2_r.txt
else
  printf "\033[1mTest failed: complex 2 size calculation\033[0m\n"
  test_failed=1
fi



exit $test_failed

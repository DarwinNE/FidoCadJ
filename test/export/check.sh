#!/bin/sh

echo "Checking that all the exported files are identical to the models."
echo
echo "Now testing:"
echo "-------------"
if diff eps/test_out.eps eps/ref/test_out.eps
then
  echo "eps     OK"
else
  echo "eps export is not conformal to the model. Please check"
fi

if diff pdf/test_out.pdf pdf/ref/test_out.pdf
then
  echo "pdf     OK"
else
  echo "pdf export is not conformal to the model. Please check"
fi

if diff pgf/test_out.pgf pgf/ref/test_out.pgf
then
  echo "pgf     OK"
else
  echo "pgf export is not conformal to the model. Please check"
fi

if diff scr/test_out.scr scr/ref/test_out.scr
then
  echo "scr     OK"
else
  echo "scr export is not conformal to the model. Please check"
fi

if diff svg/test_out.svg svg/ref/test_out.svg
then
  echo "svg     OK"
else
  echo "svg export is not conformal to the model. Please check"
fi

if diff png/test_out.png png/ref/test_out.png
then
  echo "png     OK"
else
  echo "png export is not conformal to the model. Please check"
fi

if diff jpg/test_out.jpg jpg/ref/test_out.jpg
then
  echo "jpg     OK"
else
  echo "jpg export is not conformal to the model. Please check"
fi


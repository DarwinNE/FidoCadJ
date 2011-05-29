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
  echo "svg R   OK"
else
  echo "svg export based on resolution is not conformal to the model. Please check"
fi
if diff svg/test_out_r.svg svg/ref/test_out_r.svg
then
  echo "svg S   OK"
else
  echo "svg export based on size is not conformal to the model. Please check"
fi


if diff png/test_out.png png/ref/test_out.png
then
  echo "png R   OK"
else
  echo "png export based on resolution is not conformal to the model. Please check"
fi
if diff png/test_out_r.png png/ref/test_out_r.png
then
  echo "png S   OK"
else
  echo "png export based on size is not conformal to the model. Please check"
fi

if diff jpg/test_out.jpg jpg/ref/test_out.jpg
then
  echo "jpg R   OK"
else
  echo "jpg export based on resolution is not conformal to the model. Please check"
fi

if diff jpg/test_out_r.jpg jpg/ref/test_out_R.jpg
then
  echo "jpg S   OK"
else
  echo "jpg export based on size is not conformal to the model. Please check"
fi


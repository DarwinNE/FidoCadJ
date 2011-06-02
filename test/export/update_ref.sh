#!/bin/sh

echo "This script updates all the reference files for each graphic format"

cp jpg/*.jpg jpg/ref/
cp pdf/*.pdf pdf/ref/
cp png/*.png png/ref/
cp svg/*.svg svg/ref/
cp eps/*.eps eps/ref/
cp pgf/*.pgf pgf/ref/
cp scr/*.scr scr/ref/

echo "Done"


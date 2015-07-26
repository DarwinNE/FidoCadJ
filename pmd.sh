#!/bin/bash

if [ "$1" = "" ]; then
  echo "You should specify the command to run pmd as the first argument."
  echo 'Example: ./pmd.sh ~/pmd-bin-5.0.5/bin/run.sh pmd'
  exit 1
fi 


$@ -f html -d src/ -rulesets java-basic,java-design -encoding UTF-8 >fidocadj_pmd.html

echo "PMD results stored in fidocadj_pmd.html"


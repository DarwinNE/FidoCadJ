#!/bin/bash

if [ "$1" = "" ]; then
  echo "You should specify the command to run pmd as the first argument."
  echo 'Example: ./pmd.sh ~/pmd-bin-5.0.5/bin/run.sh'
  exit 1
fi 


$@ pmd -f html -d src/ -rulesets java-basic,java-design -encoding UTF-8 >fidocadj_pmd.html

echo "PMD results stored in fidocadj_pmd.html"

$@ cpd --minimum-tokens 100 --files src/ >fidocadj_cpd.txt

echo "CPD results stored in fidocadj_cpd.txt"

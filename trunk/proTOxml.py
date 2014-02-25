#!/usr/bin/python

#Dante Loi 24/02/2014

import string
import sys

infile = open(sys.argv[1], 'r') 
outfile = open(sys.argv[2], 'w')

outfile.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n")

for line in infile.readlines():
    if line.strip():
        name, value = [ word.strip() for word in line.split("=",1) ]
        outfile.write("\t<string name=\"" + name + "\">" + value + "</string>\n")

outfile.write("</resources>")

infile.close()
outfile.close()

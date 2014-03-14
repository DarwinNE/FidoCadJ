#!/usr/bin/python

import sys

infile = open(sys.argv[1], 'r') 
outfile = open(sys.argv[2], 'w')

outfile.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n")

for line in infile:
    if line.strip():
        name, value = [ word.strip() for word in line.split("=",1) ]
        value = value.replace("&", "&amp;")
        value = value.replace("'", "\\'")
        outfile.write("\t<string name=\"" + name + "\">" + value + "</string>\n")

outfile.write("</resources>")

infile.close()
outfile.close()


#!/bin/sh

mv jar/fidocadj.jar jar/nfidocadj.jar
jarsigner -keystore compstore -signedjar jar/fidocadj.jar jar/nfidocadj.jar signFiles
rm jar/nfidocadj.jar


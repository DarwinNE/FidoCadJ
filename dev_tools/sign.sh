#!/bin/sh

mv jar/fidocadj.jar jar/nfidocadj.jar
jarsigner -keystore FidoCadJstore -signedjar jar/fidocadj.jar jar/nfidocadj.jar signFidoCadJ
rm jar/nfidocadj.jar


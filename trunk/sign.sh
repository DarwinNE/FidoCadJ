#!/bin/sh

mv jar/fidocadj.jar jar/nfidocadj.jar
jarsigner -keystore compstore -signedjar jar/fidocadj.jar jar/nfidocadj.jar darwinne
rm jar/nfidocadj.jar


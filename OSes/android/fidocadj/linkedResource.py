##
## This script is intended to create an xml file named linkedResources.xml
## containing reference to symlink files and folder.
## 1. Import the existing project in eclipse.
## 2. To use put this file in the fidocadj for android root directory and execute it.
## 3. It will create a file named linkedResources.xml.
## 4. Copy the content of this file and paste in the file ".project" created by eclipse,
## between tags "<projectDescription></projectDescription>".
## 5. Refresh the project in eclipse.
##
##Author: Giuseppe Amato
##License GPLv.3

import os,os.path

_symlink="!<symlink>"
_root=os.getcwd()

def walkDir(folder,outf):
    for path in os.listdir(folder):
        if os.path.isdir(os.path.join(folder,path)):
            walkDir(os.path.join(folder,path),outf)
        else:
            name,ext=os.path.splitext(path)
            if ext=='':
                outf.write("\t<link>\n")
                outf.write("\t\t<name>%s</name>\n"%os.path.join(folder,path)[len(_root)+1:])
                outf.write("\t\t<type>2</type>\n")
                outf.write("\t\t<location>%s</location>\n"%os.path.join(folder,path))
                outf.write("\t</link>\n")
            elif ext=='.java':
                f=open(os.path.join(folder,path),'r')
                with f:
                    if f.read(10)==_symlink:
                        outf.write("\t<link>\n")
                        outf.write("\t\t<name>%s</name>\n"%os.path.join(folder,path)[len(_root)+1:])
                        outf.write("\t\t<type>1</type>\n")
                        outf.write("\t\t<location>%s</location>\n"%os.path.join(folder,path))
                        outf.write("\t</link>\n")


outf=open("linkedResources.xml",'w')
with outf:
    outf.write("<linkedResources>\n")
    walkDir(os.path.join(_root,'src'),outf)
    outf.write("</linkedResources>\n")
#!/bin/sh
###########################################
##                                       ##
## Distro independant FidoCadJ installer ##
##                                       ##
## FidoCadj installer script             ##
## 2012-June-28 by Chokewood             ##
## revision 2012/June/30                 ##
##                                       ##
###########################################

## welcome ##
thisscript=`echo ${0##*/} |awk -F . '{print $1}'` 
echo " "
echo "   welcome to the $thisscript script" 


## run some tests #########################

## do we haves
if [ `which wget |grep -c wget` -eq 0 ]
then
    echo " "
    echo "   Cannot continue, you must have wget installed"
    echo "   Abort... "
    echo " "
    exit 1
fi
if [ `which java |grep -c java` -eq 0 ] 
 then
    echo " "
    echo "   No java support found!"
    echo "   You need to install java support first"
    echo "   Use the OpenJDE version or the proprietary Sun/Oracle Java environment"
    echo "   Abort... "
    echo " "
    exit 1
fi

## OK ##

## dit we use a switch?
if [ $# -eq 0 ] ; then
echo " "
echo "   Usage: ${0##*/} [Option] see --help"
echo " "
exit 0
fi



## functions ##############################


root_stub() {
if [ "$(id -u)" != "0" ]; then # !=
    echo " "
    echo "   This $thisscript script must be run as root " # this is the "nope,ya'aint" section
    echo "   Cannot continue, abort... "
    echo " "
    exit 1
fi
}

net_check() {
if [ `ping -c1 download.sourceforge.net |grep -c rtt` -eq 0 ]
then
    echo " "
    echo "   Cannot reach the SourceForge server!"
    echo "   Abort... "
    echo " "
    exit 1
fi
}

## get the jar
make_jarinstall() {
      mkdir -p /usr/share/java/fidocadj
      wget -q -O /usr/share/java/fidocadj/fidocadj.jar http://download.sourceforge.net/fidocadj/files/fidocadj.jar
}


## create the posix compliant desktop entry
make_desktop() {
desktopfile=/tmp/new.dsk
      echo "#!/usr/bin/env xdg-open" > $desktopfile
      echo "[Desktop Entry]" >> $desktopfile
      echo "Name=FidoCadj" >> $desktopfile
      echo "Comment=Designing circuit drawings and PCB routing" >> $desktopfile
      echo "Icon=fidocadj" >> $desktopfile
      echo "Type=Application" >> $desktopfile
      echo "Categories=Application;Development;" >> $desktopfile
      echo "Encoding=UTF-8" >> $desktopfile
      echo "Exec=fidocadj %u" >> $desktopfile
      echo "Terminal=false" >> $desktopfile
      echo "MultipleArgs=false" >> $desktopfile
      echo "StartupNotify=false" >> $desktopfile
cp -f /tmp/new.dsk /usr/share/applications/fidocadj.desktop
}

## shared icon object create
make_icon() {
	mkdir -p /usr/share/fidocadj/
	mkdir -p /usr/share/pixmaps/
        wget -q -O /usr/share/fidocadj/fidocadj.png http://sourceforge.net/projects/fidocadj/files/misc/icon_fidocadj_128x128.png
	ln -sf /usr/share/fidocadj/fidocadj.png /usr/share/pixmaps/fidocadj.png
}

## get the main shell, and make it executable
make_shell() {
wget -q -O /tmp/mainshellscript.tar.gz http://sourceforge.net/projects/fidocadj/files/misc/mainshellscript.tar.gz
tar xzfm /tmp/mainshellscript.tar.gz -C /usr/bin
chown 0:0 /usr/bin/fidocadj
chmod 755 /usr/bin/fidocadj
}

## get our doxz in place
make_docs() {
wget -q -O /tmp/maindocs.tar.gz http://sourceforge.net/projects/fidocadj/files/misc/maindocs.tar.gz
mkdir -p /usr/share/doc/fidocadj
tar xzfm /tmp/maindocs.tar.gz -C /usr/share/doc/fidocadj
chown -R 0:0 /usr/share/doc/fidocadj
chmod 644 /usr/share/doc/fidocadj/*
}

## Start ##################################
## globals

## run the installer from the switches
do_install() {
root_stub
net_check

## words of welcome
    echo " "
    echo "   This script installs the FidoCadJ runtime application and a desktop entry for you"
    echo "   You must have a working internet connection up and running in order to fetch the"
    echo "   latest files from our project folder on the SourceForge server"
    echo " "
read -p "   Do you want to install FidoCadJ on this system? [Y/n] " prompt
	prompt=`echo "$prompt" | tr "[:upper:]" "[:lower:]"`
	if [ "$prompt" == "n" ]
	  then
	     echo "   Aborted...\033[0m"
	      exit 0 
	 fi
echo " "


## Stage1 first we get the jar
echo "==>   Created /usr/share/java/fidocadj"
echo "==>   Fetching the main jar, please wait.... "
  make_jarinstall
echo "==>   Stored: /usr/share/java/fidocadj/fidocadj.jar"
## Stage2 create desktop entry
echo "==>   Creating Destop entry for common desktops.... "
  make_desktop
## Stage3 icon stuff
echo "==>   Fetching iconfile, please wait.... "
  make_icon
## Stage4 main shell executable
echo "==>   Extracting application script.... "
  make_shell
echo "==>   Extracting documents in /usr/share/doc/fidocadj"
  make_docs
## words of wishdom
    echo " "
echo "   All done..., The installation of FidoCadJ is now complete Enjoy FidoCadJ..."
echo "   Start FidoCadJ with command: fidocadj or from your desktop menu"
    echo " "
exit 0
}

# Oh-Noo, I get zapped
do_remove() {
root_stub
    echo " "
read -p "   Do you want to uninstall FidoCadJ on this system? [N/y] " prompt
prompt=`echo "$prompt" | tr "[:upper:]" "[:lower:]"`
if [ "$prompt" == "y" ]
  then
    echo "==>   Removing FidoCadJ..."
    rm -rf /usr/share/java/fidocadj/fidoca*
    rm -rf /usr/share/doc/fidocadj*
    rm -f /usr/share/applications/fidocadj.desktop
    rm -f /usr/bin/fidocadj
    rm -f /usr/share/pixmaps/fidocadj.png
    rm -f /usr/share/fidocadj/fidocadj.png
    echo "   All done  "
  else
    echo "   Aborted..."
    exit 1 
 fi
}

# only overwrite the jar, in future release this should also get new manuals 
do_update() {
root_stub
net_check
      if [ -f /usr/bin/fidocadj  ]
      then
	echo "==>   Upgrading /usr/share/java/fidocadj"
	echo "==>   Fetching the main jar, please wait.... "
	wget -q -O /usr/share/java/fidocadj/fidocadj.jar http://download.sourceforge.net/fidocadj/files/fidocadj.jar
	echo " "
	echo "   All done  " 
      else
	echo " "
	echo "   Error: Use --install option instead, /usr/bin/fidocadj not found.... "
	echo " "
	exit 1
      fi
}

########################################################################  echo -e "\033[40;1;37m witte reus\033[0m"
#### SWITCH OPTIONS    ####
# HELP, as in help, you know --help
case "$1" in
        --help )
    echo " "
    echo "    Usage: ${0##*/} [Option]"
    echo " "
    echo "    This script installs the FidoCadJ runtime application and a desktop entry for you"
    echo "    You must have a working internet connection up and running in order to fetch the"
    echo "    latest files from our project folder on the SourceForge server"
    echo " "	    
    echo " "
    echo " 	${0##*/} --install      installs FidoCadJ"
    echo " 	${0##*/} --uninstall    uninstalls FidoCadJ"
    echo " 	${0##*/} --update       updates your FidoCadJ jar file"
    echo " "
    echo "      Needless to say you must be root to install FidoCadJ..."
    echo ""
    echo " This installer operates on the following directories and files"
    echo ""
    echo "-----------------------------[DIR]------------------------------------"
    echo "/usr/share/java/fidocadj/                   Java resources"
    echo "/usr/share/fidocadj/                        Common resources"
    echo "/usr/share/doc/fidocadj/                    Manuals in pdf"
    echo "----------------------------[FILES]-----------------------------------"
    echo "/usr/bin/fidocadj                           Launch script"
    echo "/usr/share/applications/fidocadj.desktop    Desktop description file"
    echo "/usr/share/pixmaps/fidocadj.png             Icon file"
    echo "----------------------------------------------------------------------"
    echo " "
    exit 0
   ;;
 
	--install )
	      do_install
	      exit 0
	  ;;

        --uninstall )
	      do_remove
	      exit 0
	    ;;

	 --update )
	      do_update
	      exit 0
	    ;;

*) echo " "
   echo "   Error: Switch $1 is not processed..."
   echo " "
   ;;
esac           

########################################################################

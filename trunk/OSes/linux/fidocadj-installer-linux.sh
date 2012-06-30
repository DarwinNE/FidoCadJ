#!/bin/sh
##
##
## FidoCadj installer script
## 2012-June-28 by Chokewood
##
#######
thisscript=`echo ${0##*/} |awk -F . '{print $1}'` 
echo -e "\033[40;1;37m "
echo "   welcome to the $thisscript script" 


## run some tests

## do we haves
if [ `which wget |grep -c wget` -eq 0 ]
then
    echo -e "\033[40;1;31m "
    echo "   Cannot continue, you must have wget installed"
    echo "   Abort... "
    echo -e "\033[0m  " # resetcolor
    exit 1
fi
if [ `which java |grep -c java` -eq 0 ] 
 then
    echo -e "\033[40;1;31m "
    echo "   No java support found!"
    echo "   You need to install java support first"
    echo "   Use the OpenJDE version or the proprietary Sun/Oracle Java environment"
    echo "   Abort... "
    echo -e "\033[0m  " # resetcolor   
    exit 1
fi

## OK ##

## dit we use a switch?
if [ $# -eq 0 ] ; then
echo -e "\033[40;1;37m "
echo "   Usage: ${0##*/} [Option] see --help"
echo -e "\033[0m  " # resetcolor
exit 0
fi



####################### functions ##############################


root_stub() {
if [ "$(id -u)" != "0" ]; then # !=
    echo -e "\033[40;1;31m "
    echo "   This $thisscript script must be run as root " # this is the "nope,ya'aint" section
    echo "   Cannot continue, abort... "
    echo -e "\033[0m  " # resetcolor
    exit 1
fi
}

net_check() {
if [ `ping -c1 download.sourceforge.net |grep -c rtt` -eq 0 ]
then
    echo -e "\033[40;1;31m "
    echo "   Cannot reach the SourceForge server!"
    echo "   Abort... "
    echo -e "\033[0m  " # resetcolor
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
}

## shared icon object create
make_icon() {
	mkdir -p /usr/share/fidocadj/
	mkdir -p /usr/share/pixmaps/
        wget -q -O /usr/share/fidocadj/fidocadj.png http://sourceforge.net/projects/fidocadj/files/misc/icon_fidocadj_128x128.png
	ln -sf /usr/share/fidocadj/fidocadj.png /usr/share/pixmaps/fidocadj.png
}

## build the main shell executable
make_shell() {
      echo -e "#!/bin/sh" > $tempfile
      echo -e " " >> $tempfile
      echo -e "notty=\0140tty | sed -e \0042s:/dev/::\0042 |cut -c1\0140" >> $tempfile 
      echo -e "if [ \0042\0044notty\0042 == \0042t\0042 ]" >> $tempfile
      echo -e "    then" >> $tempfile
      echo -e "      echo \0042This Java program requires the graphical desktop environment\0042" >> $tempfile
      echo -e "      exit 1" >> $tempfile
      echo -e "fi" >> $tempfile
      echo -e "if [ \0140which java | grep -c java\0140 -eq 0 ]" >> $tempfile
      echo -e "    then" >> $tempfile
      echo -e "      echo \0042No java support found, abort....\0042" >> $tempfile
      echo -e "      exit 1" >> $tempfile
      echo -e "fi" >> $tempfile
      echo -e "### set the jar location to your need" >> $tempfile
      echo -e "runapp=/usr/share/java/fidocadj/fidocadj.jar" >> $tempfile
      echo -e "## sun/oracel or OpenJDK/OpenJDE" >> $tempfile 
      echo -e "if [ \0140env |grep JAVA |grep -c openj\0140 -eq 1 ]" >> $tempfile
      echo -e "  then " >> $tempfile
      echo -e "      export _JAVA_OPTIONS=-Dawt.useSystemAAFontSettings=on " >> $tempfile
      echo -e "  else" >> $tempfile
      echo -e "      export _JAVA_OPTIONS=-Dsun.java2d.opengl=true " >> $tempfile
      echo -e "fi " >> $tempfile
      echo -e "## check environment is properly set, else go blunt " >> $tempfile
      echo -e "if [ \0140env |grep -c JAVA\0140 -eq 1 ] " >> $tempfile
      echo -e "  then " >> $tempfile
      echo -e "        \0044JAVA_HOME/bin/java -jar \0044runapp" >> $tempfile
      echo -e "  else" >> $tempfile
      echo -e "   javapath=\0140which java\0140 " >> $tempfile
      echo -e "   \0044javapath -jar \0044runapp " >> $tempfile
      echo -e "fi" >> $tempfile
}

## followup on create shell make things work
make_permission() {
	  cp -f $tempfile /usr/bin/fidocadj
	  chmod 755 /usr/bin/fidocadj
}

##################### Start
## globals
desktopfile=/usr/share/applications/fidocadj.desktop
tempfile=/tmp/fidocadj_temp


##### run the installer from the switches
do_install() {
root_stub
net_check

### words of welcome
    echo -e "\033[40;1;37m " 
    echo "   This script installs the FidoCadj runtime application and a desktop entry for you"
    echo "   You must have a working internet connection up and running in order to fetch the"
    echo "   latest files from our project folder on the SourceForge server"
    echo " "
read -p "   Do you want to install FidoCadj on this system? [Y/n] " prompt
	prompt=`echo "$prompt" | tr "[:upper:]" "[:lower:]"`
	if [ "$prompt" == "n" ]
	  then
	     echo -e "\033[40;1;31m   Aborted...\033[0m"
	      exit 0 
	 fi
echo -e "\033[0m  " # resetcolor

# in the future release this should also get the manuals 
## Stage1 first we get the jar
echo -e "\033[40;1;33m==>   Created /usr/share/java/fidocadj"
echo -n "==>   Fetching the main jar, please wait.... "
  make_jarinstall
echo "done"
echo "==>   Stored: /usr/share/java/fidocadj/fidocadj.jar"
## Stage2 create desktop entry
echo -n "==>   Creating Destop entry for common desktops.... "
  make_desktop
echo "done"
## Stage3 icon stuff
echo -n "==>   Fetching iconfile, please wait.... "
  make_icon
echo "done"
## Stage4 main shell executable
echo -n "==>   Creating application script.... "
  make_shell
echo "done"
  make_permission
echo "==>   Altered filepermissions for /usr/bin/fidocadj"
## words of wishdom
echo -e " \033[0m"
echo -e "\033[40;1;37m   All done..., Enjoy FidoCadj..."
echo "   Start FidoCadj with command: fidocadj or from your desktop menu"
echo -e "\033[0m  " # resetcolor
exit 0
}

# Oh-Noo, I get zapped
do_remove() {
root_stub
echo -e "\033[40;1;37m"
read -p "   Do you want to uninstall FidoCadj on this system? [N/y] " prompt
prompt=`echo "$prompt" | tr "[:upper:]" "[:lower:]"`
if [ "$prompt" == "y" ]
  then
    echo -e "\033[40;1;33m==>   Removing FidoCadj..."
    rm -rf /usr/share/java/fidocadj/fidoca*
    rm -f /usr/share/applications/fidocadj.desktop
    rm -f /usr/bin/fidocadj
    rm -f /usr/share/pixmaps/fidocadj.png
    rm -f /usr/share/fidocadj/fidocadj.png
    echo -e "\033[40;1;37m   All done\033[0m  " # resetcolor
  else
    echo -e "\033[40;1;31m   Aborted...\033[0m"
    exit 1 
 fi
}

# only overwrite the jar, in future release this should also get new manuals 
do_update() {
root_stub
net_check
      if [ `which fidocadj |grep -c fidocadj` -eq 1 ]
      then
	echo -e "\033[40;1;37m==>   Upgrading /usr/share/java/fidocadj"
	echo -ne "\033[40;1;32m==>   Fetching the main jar, please wait.... "
	wget -q -O /usr/share/java/fidocadj/fidocadj.jar http://download.sourceforge.net/fidocadj/files/fidocadj.jar
	echo " "
	echo -e "\033[40;1;37m   All done\033[0m  " # resetcolor
      else
	 echo -e "\033[40;1;31m " # set red;
	echo "   Error: Use --install option instead, /usr/bin/fidocadj not found.... "
	echo -e "\033[0m  " # resetcolor
	exit 1
      fi
}

########################################################################  echo -e "\033[40;1;37m witte reus\033[0m"
#### SWITCH OPTIONS    ####
# HELP, as in help, you know --help
case "$1" in
        --help )
    echo -e "\033[40;1;37m " # set white;
    echo "    Usage: ${0##*/} [Option]"
    echo " "
    echo "    This script installs the FidoCadj runtime application and a desktop entry for you"
    echo "    You must have a working internet connection up and running in order to fetch the"
    echo "    latest files from our project folder on the SourceForge server"
    echo " "	    
    echo " "
    echo " 	${0##*/} --install      installs FidoCadj"
    echo " 	${0##*/} --uninstall    uninstalls FidoCadj"
    echo " 	${0##*/} --update       updates your FidoCadj jar file"
    echo " "
    echo "      Needless to say you must be root to install FidoCadj..."
    echo -e "\033[0m  " # resetcolor
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
   echo -e "\033[40;1;31m   Error: Switch $1 is not processed...\033[0m"
   echo " "
   ;;
esac           

########################################################################

#!/bin/sh
###########################################
##                                       ##
## Distro independant FidoCadJ installer ##
##                                       ##
## FidoCadj installer script             ##
## 2012-June-28 by Chokewood             ##
## revision 2012/Juli/03                 ##
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
    echo "   Please install wget first... "
    echo " "
    exit 1
fi
if [ `which java |grep -c java` -eq 0 ] 
 then
    echo " "
    echo "   No Java support found!"
    echo "   You need to install Java support first"
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


## make the core
make_core() {

	mkdir -p /usr/share/fidocadj/
	mkdir -p /usr/share/doc/fidocadj/
	mkdir -p /usr/share/java/fidocadj/
	mkdir -p /usr/share/applications/
	wget -q -O /tmp/main-linux-package.tgz http://sourceforge.net/projects/fidocadj/files/misc/main-linux-package.tgz
	# <-- testing --->
	#wget -q -O /tmp/main-linux-package.tgz http://www.2b-art-design.nl/files/test/main-linux-package.tgz 
	tar xzfm /tmp/main-linux-package.tgz -C /usr
	chown 0:0 /usr/bin/fidocadj
	chmod 755 /usr/bin/fidocadj
	ln -sf /usr/share/fidocadj/fidocadj.png /usr/share/pixmaps/fidocadj.png
	xdg-icon-resource install --context mimetypes --size 48 /usr/share/fidocadj/fidocadj-file.png x-application-fidocadj
	xdg-mime install /usr/share/fidocadj/fidocadj-mime.xml
	update-mime-database /usr/share/mime
}



## get our docs check if we need french or italian else drop to english
make_docs() {
# what language for manuals
get_lang=`env |grep -w LANG |cut -c6-7`
if [ "$get_lang" == "fr" ]
then 
  wget -q -O /usr/share/doc/fidocadj/fidocadj_manual_fr.pdf http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_fr.pdf
  echo "Your French manual is installed in /usr/share/doc/fidocadj/"
elif [ "$get_lang" == "it" ]
then
  wget -q -O /usr/share/doc/fidocadj/fidocadj_manual_it.pdf http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_it.pdf
  echo "Your Italian manual is installed in /usr/share/doc/fidocadj/"
##

#### duplicate and adapt this block for future languages
#elif [ "$get_lang" == "XX" ]
#then
#  wget -q -O /usr/share/doc/fidocadj/fidocadj_manual_XX.pdf http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_XX.pdf
#  echo "Your X-Lang manual is installed in /usr/share/doc/fidocadj/"
####

##
else
  echo "There are no native manuals available, but an english manual is available in /usr/share/doc/fidocadj/"
fi

 wget -q -O /usr/share/doc/fidocadj/fidocadj_manual_en.pdf http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_en.pdf

wget -q -O /usr/share/doc/fidocadj/README http://sourceforge.net/projects/fidocadj/files/README
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
echo "==>   Get the FidoCadJ core"
make_core
make_jarinstall
echo "==>   Get the documents in /usr/share/doc/fidocadj"
make_docs
## words of wishdom
    echo " "
echo "   All done..., The installation of FidoCadJ is now complete Enjoy FidoCadJ..."
echo "   Start FidoCadJ with command: fidocadj or from your desktop menu"
echo " "
echo "   Your mimetype settings may require to re-login, but this is not essential to start working with FidoCadJ"
echo " "
echo "   Store this installer on a safe place if you wish to update FidoCadJ in the future "
echo "   or to completely remove FidoCadJ from your system."
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
    xdg-mime uninstall /usr/share/fidocadj/fidocadj-mime.xml
    xdg-icon-resource uninstall --context mimetypes --size 48 /usr/share/fidocadj/fidocadj-file.png x-application-fidocadj
    rm -rf /usr/share/java/fidocadj/fidoca*
    rm -rf /usr/share/doc/fidocadj*
    rm -f /usr/share/applications/fidocadj.desktop
    rm -f /usr/bin/fidocadj
    rm -f /usr/share/pixmaps/fidocadj.png
    rm -rf /usr/share/fidocad*
    update-mime-database /usr/share/mime
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
    echo "   This installer operates on the following directories and files"
    echo ""
    echo "   -----------------------------[DIR]------------------------------------"
    echo "   /usr/share/java/fidocadj/                   Java resources"
    echo "   /usr/share/fidocadj/                        Common resources"
    echo "   /usr/share/doc/fidocadj/                    Manuals in pdf"
    echo "   ----------------------------[FILES]-----------------------------------"
    echo "   /usr/bin/fidocadj                           Launch script"
    echo "   /usr/share/applications/fidocadj.desktop    Desktop description file"
    echo "   /usr/share/pixmaps/fidocadj.png             Icon file (symbolic-link) "
    echo "   ----------------------------------------------------------------------"
    echo " "
    echo "   It also adds mimetype settings for FidoCadJ"
    echo " "
    echo "   On uninstall, all these files and mime-entries will be removed"
    echo " "
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

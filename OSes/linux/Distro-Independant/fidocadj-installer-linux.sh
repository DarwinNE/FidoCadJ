#!/bin/bash

###########################################
##                                       ##
## Distro independant FidoCadJ installer ##
##                                       ##
## FidoCadj installer script             ##
## 2012-June-28 by Chokewood             ##
## revision 2012/Juli/03                 ##
## revision 2014/April/30 by Dante       ##
## 						                 ##
###########################################

## welcome ##
thisscript=`echo ${0##*/} | awk -F . '{print $1}'` 
echo " "
echo "   Welcome to the $thisscript script" 

############################### TESTS ##########################################

## script's options test
if [ $# -eq 0 ]
then
	echo " "
	echo "   Usage: ${0##*/} [Option] see --help"
	echo " "
	exit 0
fi

## wget availability test
if [ `which wget | grep -c wget` -eq 0 ]
then
    echo " "
    echo "   Cannot continue, you must have wget installed"
    echo "   Please install wget first... "
    echo "Abort... "
    echo " "
    exit 1
fi

## java availability test
if [ `which java | grep -c java` -eq 0 ] 
then
    echo " "
    echo "   No Java support found!"
    echo "   You need to install Java support first"
    echo "   Use the OpenJDE version or the proprietary Sun/Oracle Java environment"
    echo "Abort... "
    echo " "
    exit 2
fi

## connection availability test
ping -c1 download.sourceforge.net > /dev/null
if [ $? -ne 0 -a $1 != "--uninstall" ]
then
    echo " "
    echo "   Cannot reach the SourceForge server!"
    echo "Abort... "
    echo " "
    exit 3
fi

## super user test
if [ $(id -u) != 0 ]; 
then
    echo " "
    echo "   This $thisscript script must be run as root "
    echo "   Cannot continue, abort... "
    echo " "
    exit 4
fi

################################################################################

######################### UTILITY FUNCTIONS ####################################

## get the jar
make_jarinstall() {
	mkdir -p /usr/share/java/fidocadj

    wget -q -O /usr/share/java/fidocadj/temp.jar http://download.sourceforge.net/fidocadj/files/fidocadj.jar
	if [ $? -eq 0 ]
	then
		mv /usr/share/java/fidocadj/temp.jar /usr/share/java/fidocadj/fidocadj.jar
	else
		rm /usr/share/java/fidocadj/temp.jar
		echo " "
		echo "   Error: http://download.sourceforge.net/fidocadj/files/fidocadj.jar not available. "
		echo " "
		exit 5
	fi
}

## make the core
make_core() {
	mkdir -p /usr/share/fidocadj/
	mkdir -p /usr/share/doc/fidocadj/
	mkdir -p /usr/share/java/fidocadj/
	mkdir -p /usr/share/applications/

	wget -q -O /tmp/main-linux-package.tgz http://sourceforge.net/projects/fidocadj/files/misc/main-linux-package.tgz

	if [ $? -eq 0 ]
	then
		tar xzfm /tmp/main-linux-package.tgz -C /usr
		chown 0:0 /usr/bin/fidocadj
		chmod 755 /usr/bin/fidocadj
		ln -sf /usr/share/fidocadj/fidocadj.png /usr/share/pixmaps/fidocadj.png
		xdg-icon-resource install --context mimetypes --size 48 /usr/share/fidocadj/fidocadj-file.png x-application-fidocadj
		xdg-mime install /usr/share/fidocadj/fidocadj-mime.xml
		update-mime-database /usr/share/mime &> /dev/null
		echo "   Saved: /tmp/main-linux-package.tgz "
	else
		echo " "
		echo "   Error: main-linux-package.tgz not available. "
		echo " "
		exit 6
	fi
}

## remove the core
purge_me() {
    echo "==>   Removing FidoCadJ..."
    xdg-mime uninstall /usr/share/fidocadj/fidocadj-mime.xml
    xdg-icon-resource uninstall --context mimetypes --size 48 /usr/share/fidocadj/fidocadj-file.png x-application-fidocadj
    rm -rf /usr/share/java/fidocadj/fidoca*
	echo "   Removed: /usr/share/java/fidocadj/fidoca* "
    rm -rf /usr/share/doc/fidocadj*
	echo "   Removed: /usr/share/doc/fidocadj* "
    rm -f /usr/share/applications/fidocadj.desktop
	echo "   Removed: /usr/share/applications/fidocadj.desktop "
    rm -f /usr/bin/fidocadj
	echo "   Removed: /usr/bin/fidocadj "
    rm -f /usr/share/pixmaps/fidocadj.png
	echo "   Removed: /usr/share/pixmaps/fidocadj.png "
    rm -rf /usr/share/fidocad*
	echo "   Removed: /usr/share/fidocad* "
    update-mime-database /usr/share/mime &> /dev/null
}

## get the docs 
make_docs() {

	case ${LANGUAGE:0:2} in
		en )
		 	wget -q -O /usr/share/doc/fidocadj/fidocadj_manual_en.pdf http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_en.pdf
			if [ $? -eq 0 ]
			then
  				echo "   Your English manual is installed in /usr/share/doc/fidocadj/"
			else
				echo "http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_en.pdf not available. "
				exit 7 
			fi
		  ;;

		fr )
  			wget -q -O /usr/share/doc/fidocadj/fidocadj_manual_fr.pdf http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_fr.pdf
			if [ $? -eq 0 ]
			then
  				echo "   Your French manual is installed in /usr/share/doc/fidocadj/"
			else
				echo "http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_fr.pdf not available. "
				exit 7 
			fi
		  ;;

		it )
  			wget -q -O /usr/share/doc/fidocadj/fidocadj_manual_it.pdf http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_it.pdf
			if [ $? -eq 0 ]
			then
  				echo "   Your Italian manual is installed in /usr/share/doc/fidocadj/"
			else
				echo "http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_it.pdf not available. "
				exit 7 
			fi
		  ;;

		zh )
			wget -q -O /usr/share/doc/fidocadj/fidocadj_manual_it.pdf http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_zh.pdf
			if [ $? -eq 0 ]
			then
  				echo "   Your Chinese manual is installed in /usr/share/doc/fidocadj/"
			else
				echo "http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_zh.pdf not available. "
				exit 7 
			fi
		  ;;

  		* )
  			echo "   There are no native manuals available, but an english manual is available in /usr/share/doc/fidocadj/"

		 	wget -q -O /usr/share/doc/fidocadj/fidocadj_manual_en.pdf http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_en.pdf
			if [ $? -eq 0 ]
			then
  				echo "   Your English manual is installed in /usr/share/doc/fidocadj/"
			else
				echo "http://sourceforge.net/projects/fidocadj/files/manuals/fidocadj_manual_en.pdf not available. "
				exit 7 
			fi
		  ;;

	esac
			wget -q -O /usr/share/doc/fidocadj/README http://sourceforge.net/projects/fidocadj/files/README

			chown -R 0:0 /usr/share/doc/fidocadj
			chmod 644 /usr/share/doc/fidocadj/*
}

################################################################################

########################## OPTIONS FUNCTIONS ###################################

do_install() {

	## words of welcome
   	echo " "
   	echo "   This script installs the FidoCadJ runtime application and a desktop entry for you"
   	echo "   You must have a working internet connection up and running in order to fetch the"
   	echo "   latest files from our project folder on the SourceForge server"
    echo " "

	read -p "   Do you want to install FidoCadJ on this system? [Y/n] " prompt
	if [ "$prompt" == "n" -o "$prompt" == "N" ] 
	then
	   echo "Aborted..."
 	else
		echo " "
		echo "==>   Get the FidoCadJ core"
		make_core
		make_jarinstall
		echo "==>   Get the documents in /usr/share/doc/fidocadj"
		make_docs

		## words of wishdom
    	echo " "
		echo "   The installation of FidoCadJ is now complete Enjoy FidoCadJ..."
		echo "   Start FidoCadJ with command: fidocadj or from your desktop menu"
		echo " "
		echo "   Your mimetype settings may require to re-login, but this is not essential to start working with FidoCadJ"
		echo " "
		echo "   Store this installer on a safe place if you wish to update FidoCadJ in the future "
		echo "   or to completely remove FidoCadJ from your system."
    	echo " "
		echo "All done.  "
	fi 
	exit 0
}

do_remove() {
    echo " "
	read -p "   Do you want to uninstall FidoCadJ on this system? [N/y] " prompt
	if [ $prompt == "y" -o $prompt == "Y" ]
	then
		purge_me
    	echo " "
		echo "All done. "
 	else
    	echo "Aborted..."
 	fi
	exit 0
}

do_update() {
    if [ -f /usr/bin/fidocadj ]
    then
		echo "==>   Upgrading /usr/share/java/fidocadj"
		echo "==>   Fetching the main jar, please wait.... "
		wget -q -O /usr/share/java/fidocadj/temp.jar http://download.sourceforge.net/fidocadj/files/fidocadj.jar
		if [ $? -eq 0 ]
		then 
			rm /usr/share/java/fidocadj/fidocadj.jar
			mv /usr/share/java/fidocadj/temp.jar /usr/share/java/fidocadj/fidocadj.jar
			echo "   Updated: /usr/share/java/fidocadj/fidocadj.jar "
			echo "==>   Upgrading the the docs "
			make_docs
    		echo " "
			echo "All done. "
			exit 0
		else
			rm /usr/share/java/fidocadj/temp.jar
			echo " "
			echo "   Error: http://download.sourceforge.net/fidocadj/files/fidocadj.jar not available. "
			echo " "
			exit 8
		fi 
    else
		echo " "
		echo "   Error: Use --install option instead, /usr/bin/fidocadj not found.... "
		echo " "
		exit 8
    fi
}

do_restore() {
    if [ -f /usr/bin/fidocadj  ]
    then
		echo "==>   Upgrading /usr/share/java/fidocadj"
		echo "==>   Fetching the main jar, please wait.... "
		wget -q -O /usr/share/java/fidocadj/$1.jar https://sourceforge.net/projects/fidocadj/files/Older%20versions/$1/fidocadj.jar
		if [ $? -eq 0 ]
		then 
			rm /usr/share/java/fidocadj/fidocadj.jar
			mv /usr/share/java/fidocadj/$1.jar /usr/share/java/fidocadj/fidocadj.jar
			echo "   Saved: /usr/share/java/fidocadj/fidocadj.jar "
			echo " "
			echo "All done. "
			exit 0
		else
			rm /usr/share/java/fidocadj/$1.jar
			echo " "
			echo "   Error: $1 not is a FidoCadj version. "
			echo " "
			exit 9
		fi 
    else
		echo " "
		echo "   Error: Use --install option instead, /usr/bin/fidocadj not found.... "
		echo " "
		exit 9
    fi
}

do_help() {
	echo " "
    echo "    Usage: ${0##*/} [Option]"
    echo " "
    echo "    This script installs the FidoCadJ runtime application and a desktop entry for you"
    echo "    You must have a working internet connection up and running in order to fetch the"
    echo "    latest files from our project folder on the SourceForge server"
    echo " "	    
    echo " "
    echo " 	${0##*/} --install      	   installs FidoCadJ"
    echo " 	${0##*/} --uninstall    	   uninstalls FidoCadJ"
    echo " 	${0##*/} --update       	   updates your FidoCadJ jar file"
    echo " 	${0##*/} --restore [VERSION]    restore your FidoCadJ jar file to an old version"
    echo " "
    echo "      Needless to say you must be root to install FidoCadJ..."
    echo " "
    echo "   This installer operates on the following directories and files"
    echo " "
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
}

################################################################################

case $1 in
    --help )
    	do_help

	  ;;
 
	--install )
	    do_install
	  ;;

    --uninstall )
		do_remove
	  ;;

	--update )
		do_update
	  ;;

	--restore )
		do_restore $2
	  ;;

	*) 
		echo " "
	   	echo "   Error: Switch $1 is not processed..."
   		echo " "
   	  ;;
esac           

exit 0

################################################################################

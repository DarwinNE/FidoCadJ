<?php
//this file contains the definitions of page download.php

//description attribute of the download page
define("DOWNLOAD_PAGE_DESCRIPTION", "Scarica FidoCadJ gratuitamente. Nessuna registrazione o login. Disponibile per Windows, MacOSX, Linux e Android. Unisciti a noi nello sviluppo su GitHub!");

//title attribute of the download page
define("TITLE_PAGE_DESCRIPTION", "Download di FidoCadJ");

//title of the first table
define ("TITLE_TABLE_1","Scarica gratuitamente la versione stabile: ".CURRENT_VERSION);

//stable label (to indicate the version is stable and ready to use)
define ("STABLE","stabile");
define ("ALMOST_STABLE","quasi stabile");

//logos alt attribute
define ("WINDOWS_LOGO_ALT_ATTRIBUTE","logo di Windows");
define ("MAC_LOGO_ALT_ATTRIBUTE","logo di MacOS");
define ("LINUX_LOGO_ALT_ATTRIBUTE","logo di Linux");
define ("GIT_HUB_LOGO_ALT_ATTRIBUTE","logo di GitHub");
define ("ANDROID_LOGO_ALT_ATTRIBUTE","logo di Android");

//descriptions in the first table
define ("WINDOWS_VERSION_DESCRIPTION","Windows installer (Java ".JAVA_VERSION_REQUIRED." minimo).");
define ("MAC_VERSION_DESCRIPTION","MacOSX software (>10.7.3, Java ".JAVA_VERSION_REQUIRED." minimo). Apri l'immagine del disco e copiala nella tua cartella Applicazioni.");
define ("LINUX_VERSION_DESCRIPTION","Linux e altri sistemi operativi (richiesto Java ".JAVA_VERSION_REQUIRED."). Prova a fare doppio click sul file. Oppure digita nella shell:");

//name and description about user manual in current version table
define ("USER_MANUAL_NAME","Manuale dell'utente (pdf)");
define ("USER_MANUAL_DESCRIPTION","Il manuale per gli utenti di FidoCadJ in inglese.");

//the text that appear after the first table
define ("TROUBLE_WITH_JAVA_TEXT","Se si apre fidocadj.jar con un software zip, probabilmente non hai Java installato sul tuo PC. Puoi scaricarlo gratuitamente dal <a href='http://www.java.com/it/download/index.jsp'>Sito Web di Oracle</a>.");

//title of second table
define ("TITLE_TABLE_2","Attualmente in lavorazione");

define ("GIT_HUB_REPO_DESCRIPTION","Il progetto FidoCadJ su GitHub: invia segnalazioni su malfunzionamenti, richiedi una caratteristica particolare, dai un'occhiata al nostro codice sorgente...");

define ("ANDROID_VERSION", CURRENT_ANDROID_VERSION." (".ALMOST_STABLE.")");
define ("ANDROID_VERSION_DESCRIPTION","Una versione preliminare di FidoCadJ per Android&#8482; (4.0 minimo). Quasi stabile, ogni suggerimento è beneaccetto.");

define ("TITLE_TABLE_3","Versioni stabili: ".OLD_STABLE_VERSION);
define("SUBTITLE_TABLE_3","Necessitano di Java ".JAVA_VERSION_REQUIRED_OLD_VERSION." minimo e possono essere utili su computer più vecchi.");

define ("WINDOWS_OLD_STABLE_VERSION_DESCRIPTION","Windows installer (Java ".JAVA_VERSION_REQUIRED_OLD_VERSION." minimo).");
define ("MAC_OLD_STABLE_VERSION_DESCRIPTION","MacOSX software (>10.4, Java ".JAVA_VERSION_REQUIRED_OLD_VERSION." minimo). Apri l'immagine del disco e copiala nella tua cartella Applicazioni.");
define ("LINUX_OLD_STABLE_VERSION_DESCRIPTION","Versione per qualsiasi sistema operativo (richiesto Java ".JAVA_VERSION_REQUIRED_OLD_VERSION."). Prova a fare doppio click sul file. Oppure digita nella shell:");

//name and description about user manual in old version table
define ("USER_MANUAL_NAME_OLD_VERSION","Manuale dell'utente (pdf)");
define ("USER_MANUAL_DESCRIPTION_OLD_VERSION","Il manuale per gli utenti di FidoCadJ in inglese.");

//warning text under the last table
define ("WARNING_GIT_HUB_MIGRATION","Attenzione: stiamo man mano spostando tutto il progetto da SourceForge su GitHub, qualche link su questo sito ancora rimanda a pagine su SourceForge.");

 ?>

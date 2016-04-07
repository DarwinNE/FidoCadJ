<?php
//this file contains the definitions of page download.php

//description attribute of the download page
define("DOWNLOAD_PAGE_DESCRIPTION", "Download FidoCadJ for free. No registration is required. Available for Windows, MacOSX, Linux and Android. Join us in the developement on GitHub!");

//title attribute of the download page
define("TITLE_PAGE_DESCRIPTION", "FidoCadJ Download");

//title of the first table
define ("TITLE_TABLE_1","Free download stable version: ".CURRENT_VERSION);

//stable label (to indicate the version is stable and ready to use)
define ("STABLE","stable");
define ("ALMOST_STABLE","almost stable");

//logos alt attribute
define ("WINDOWS_LOGO_ALT_ATTRIBUTE","Windows logo");
define ("MAC_LOGO_ALT_ATTRIBUTE","MacOS logo");
define ("LINUX_LOGO_ALT_ATTRIBUTE","Linux logo");
define ("GIT_HUB_LOGO_ALT_ATTRIBUTE","GitHub logo");
define ("ANDROID_ALT_ATTRIBUTE","Android logo");

//descriptions in the first table
define ("WINDOWS_VERSION_DESCRIPTION","Windows installer (Java ".JAVA_VERSION_REQUIRED." at least).");
define ("MAC_VERSION_DESCRIPTION","MacOSX application (>10.7.3, you need Java ".JAVA_VERSION_REQUIRED."). Open the disk image and copy FidoCadJ into your Application folder.");
define ("LINUX_VERSION_DESCRIPTION","Linux or any operating system (with at least Java ".JAVA_VERSION_REQUIRED."). Try to double click on the file. Or type from a shell:");

//name and description about user manual in current version table
define ("USER_MANUAL_NAME","User manual in english (pdf)");
define ("USER_MANUAL_DESCRIPTION","The English FidoCadJ user manual.");

//the text that appear after the first table
define ("TROUBLE_WITH_JAVA_TEXT"," If fidocadj.jar is opened with a zip utility, you probably do not have Java on your computer. You can freely download it from the <a href='http://www.java.com/it/download/index.jsp'>Oracle web site</a>.");

//title of second table
define ("TITLE_TABLE_2","Currently under development");

define ("GIT_HUB_REPO_DESCRIPTION","The FidoCadJ GitHub project: post a bug report, ask for a particular feature, checkout the source code...");

define ("ANDROID_VERSION","0.24.5 kappa (".ALMOST_STABLE.")");
define ("ANDROID_VERSION_DESCRIPTION","A preliminary version of FidoCadJ for Android&#8482; (4.0 at least). Almost stable, any feedback will be welcomed.");

define ("TITLE_TABLE_3","Stable version: ".OLD_STABLE_VERSION);
define("SUBTITLE_TABLE_3","Requires Java 1.5 at least, so it might be useful for older computers.");

define ("WINDOWS_OLD_STABLE_VERSION_DESCRIPTION","Windows installer (Java ".JAVA_VERSION_REQUIRED_OLD_VERSION." at least).");
define ("MAC_OLD_STABLE_VERSION_DESCRIPTION","MacOSX application (>10.4, requires Java ".JAVA_VERSION_REQUIRED_OLD_VERSION." at least). Open the dmg disk image, and copy the FidoCadJ application into your Application directory.");
define ("LINUX_OLD_STABLE_VERSION_DESCRIPTION","Version for any operating system (with Java version ".JAVA_VERSION_REQUIRED_OLD_VERSION." at least). Often, you can can double click on the file. Or type from a shell:");

//name and description about user manual in old version table
define ("USER_MANUAL_NAME_OLD_VERSION","User manual in english (pdf)");
define ("USER_MANUAL_DESCRIPTION_OLD_VERSION","The English FidoCadJ user manual.");

//warning text under the last table
define ("WARNING_GIT_HUB_MIGRATION","Warning: we are progressively migrating all the FidoCadJ project from SourceForge to GitHub; some links in this website still point to SourceForge pages.");

 ?>

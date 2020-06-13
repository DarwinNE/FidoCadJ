<?php
//this file contains the definitions of page download.php

//description attribute of the download page
define("DOWNLOAD_PAGE_DESCRIPTION", "Κατεβάστε το FidoCadJ δωρεάν. Δεν χρειάζεται εγγραφή. Διαθέσιμο για Windows, MacOSX, Linux και Android. Συμμετέχετε στον προγραμματισμό μαζί μας στο GitHub!");

//title attribute of the download page
define("TITLE_PAGE_DESCRIPTION", "Κατεβάστε το FidoCadJ");

//title of the first table
define ("TITLE_TABLE_1","Δωρεάν κατέβασμα σταθερής έκδοσης: ".CURRENT_VERSION);

//stable label (to indicate the version is stable and ready to use)
define ("STABLE","σταθερή");
define ("ALMOST_STABLE","σχεδόν σταθερή");

//logos alt attribute
define ("WINDOWS_LOGO_ALT_ATTRIBUTE","λογότυπο Windows");
define ("MAC_LOGO_ALT_ATTRIBUTE","λογότυπο macOS");
define ("LINUX_LOGO_ALT_ATTRIBUTE","λογότυπο Linux");
define ("GIT_HUB_LOGO_ALT_ATTRIBUTE","λογότυπο GitHub");
define ("ANDROID_LOGO_ALT_ATTRIBUTE","λογότυπο Android ");

//descriptions in the first table
define ("WINDOWS_VERSION_DESCRIPTION","Windows installer (Java ".JAVA_VERSION_REQUIRED." το ελάχιστο).");
define ("MAC_VERSION_DESCRIPTION","macOS application (>10.7.3, Java ".JAVA_VERSION_REQUIRED." το ελάχιστο). Ανοίξτε το εικονίδιο εικόνας και αντιγράψτε το FidoCadJ μέσα στον φάκελο της εφαρμογής σας.");
define ("LINUX_VERSION_DESCRIPTION","Linux ή οποιοδήποτε λειτουργικό σύστημα (με το λιγότερο έκδοση της Java ".JAVA_VERSION_REQUIRED."). Προσπαθήστε να κάνετε διπλό κλικ στο αρχείο. Η πληκτρολογείστε απο το shell:");

//name and description about user manual in current version table
define ("USER_MANUAL_NAME","Εγχειρίδιο χρήστη στα αγγλικά (pdf)");
define ("USER_MANUAL_DESCRIPTION","Το Αγγλικό FidoCadJ εγχειρίδιο χρήσης.");

//the text that appear after the first table
define ("TROUBLE_WITH_JAVA_TEXT","Εάν το fidocadj.jar ανοίγει σαν zip , πιθανόν να μην έχετε την Java εγκατεστημένη στον υπολογιστή σας. Μπορείτε να το κατεβάσετε δωρεάν από το <a href='http://www.java.com/it/download/index.jsp'>την ιστοσελίδα της Oracle</a>.");

//title of second table
define ("TITLE_TABLE_2","Προς το παρών είναι σε ανάπτυξη");

define ("GIT_HUB_REPO_DESCRIPTION","Το έργο FidoCadJ GitHub :αναρτήστε ένα σφάλμα, ζητήστε ένα νέο χαρακτηριστικό, δείτε τον πηγαίο κώδικα...");

define ("ANDROID_VERSION", CURRENT_ANDROID_VERSION . " (".ALMOST_STABLE.")");
define ("ANDROID_VERSION_DESCRIPTION","Μια αρχική έκδοση του FidoCadJ για Android&#8482; (4.0 το λιγότερο). Σχεδόν σταθερή έκδοση, οποιοδήποτε σχόλιο είναι καλοδεχούμενο.");

define ("TITLE_TABLE_3","Σταθερή έκδοση: ".OLD_STABLE_VERSION);
define("SUBTITLE_TABLE_3","Απαιτεί Java 1.5 το λιγότερο, έτσι μπορεί να είναι χρήσιμο για παλιότερους υπολογιστές.");

define ("WINDOWS_OLD_STABLE_VERSION_DESCRIPTION","Windows εγκατάσταση (Java ".JAVA_VERSION_REQUIRED_OLD_VERSION." το λιγότερο).");
define ("MAC_OLD_STABLE_VERSION_DESCRIPTION","MacOSX εφαρμογή (>10.4, απαιτεί Java ".JAVA_VERSION_REQUIRED_OLD_VERSION." το λιγότερο). Ανοίξτε το αρχείο εικόνας, και κάντε αντιγραφή της εφαρμογής FidoCadJ μέσα στον φάκελο της εφαρμογής σας.");
define ("LINUX_OLD_STABLE_VERSION_DESCRIPTION","Έκδοση για κάθε λειτουργικό σύστημα (με έκδοση Java ".JAVA_VERSION_REQUIRED_OLD_VERSION." το λιγότερο). Προσπαθήστε να κάνετε διπλό κλικ στο αρχείο. Η πληκτρολογείστε απο το shell:");

//name and description about user manual in old version table
define ("USER_MANUAL_NAME_OLD_VERSION","Εγχειρίδιο χρήσης στα ελληνικά (pdf)");
define ("USER_MANUAL_DESCRIPTION_OLD_VERSION","Το ελληνικό εγχειρίδιο χρήσης για το FidoCadJ.");

//warning text under the last table
define ("WARNING_GIT_HUB_MIGRATION","Προσοχή: προοδευτικά μεταφέρουμε ολόκληρο το έργο FidoCadJ από το SourceForge στο GitHub;κάποιοι σύνδεσμοι σε αυτόν τον ιστότοπο δείχνουν σε σελίδες του SourceForge.");

 ?>

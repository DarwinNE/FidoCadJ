<?php
//this file contains the definitions of text included in libs.html page

//HEAD
define("FAQ_DESCRIPTION","Frequently asked questions about FidoCadJ and how it works. How to download and easily use the free software to draw and share your project."); //page meta tag description

define("PAGE_TITLE","FidoCadJ Frequently Asked Questions"); //page title attribute

//CONTENT
define("PAGE_SUB_TITLE","Frequently Asked Questions");

//FAQ INDEX
define("FAQ_INDEX_1","What is FidoCadJ?");
define("FAQ_INDEX_2","Can I use FidoCadJ for other than electronics?");
define("FAQ_INDEX_3","How much does FidoCadJ cost?");
define("FAQ_INDEX_4","What are the requirements to run FidoCadJ?");
define("FAQ_INDEX_5","How can I rotate/mirror symbols?");
define("FAQ_INDEX_6","Why is FidoCadJ useful in my forum/website?");
define("FAQ_INDEX_7","I already use Kicad, LTSpice, Cadence, Mentor, Altium or Visio, why is FidoCadJ interesting?");
define("FAQ_INDEX_8","FidoCadJ does not run or is very slow, what can I do?");
define("FAQ_INDEX_9","Version number is 0.something. Is it an unstable/unfinished project?");
define("FAQ_INDEX_10","How can I participate to the development?");

//FAQ
define("FAQ_TITLE_1","What is FidoCadJ?");
define("FAQ_CONTENT_1","<p>FidoCadJ is a <b>simple</b> vector graphic editor. You can draw whatever you want, and FidoCadJ comes with a large library of symbols related to the electrical and electronic engineering.</p>");

define("FAQ_TITLE_2","Can I use FidoCadJ for other than electronics?");
define("FAQ_CONTENT_2",'<p>Yes, you can! FidoCadJ can be used in <a href="http://www.matematicamente.it/forum/viewtopic.php?f=38&t=114624">diagrams</a>, <a href="http://www.electroyou.it/pepito/wiki/libreria-flowchart-per-fidocadj">flow-charts</a> and <a href="http://www.electroyou.it/admin/wiki/peanuts-fidocadj">even comics</a>. In the <a href="http://darwinne.github.io/FidoCadJ/scrn.html">screenshots</a> you have some examples.</p>');

define("FAQ_TITLE_3","How much does FidoCadJ cost?");
define("FAQ_CONTENT_3","<p>Nothing. Zero. Nil. FidoCadJ is <b>Free Software</b>, released under the General Public License version 3.</p>");

define("FAQ_TITLE_4","What are the requirements to run FidoCadJ?");
define("FAQ_CONTENT_4","<p>You need a computer with Java ".JAVA_VERSION_REQUIRED." installed. Any operating system will do (Windows, Linux, MacOSX,...). You can also run FidoCadJ on an Android tablet or cellular phone (version 4.0 at least is required).</p>");

define("FAQ_TITLE_5","How can I rotate/mirror symbols?");
define("FAQ_CONTENT_5","<p>Press R or S while editing them. You also have Rotate/Mirror among the Edit menu items.</p>");

define("FAQ_TITLE_6","Why is FidoCadJ useful in my forum/website?");
define("FAQ_CONTENT_6","<p>You can share the source code of the drawings. It is a simple plain text format completely described in the FidoCadJ user <a href='https://github.com/DarwinNE/FidoCadJ/releases/download/v0.24.5/manual_en.pdf'>manual</a>. Your users can pick it up, modifying the drawings as they wish and then upload them again.</p>");

define("FAQ_TITLE_7","I already use Kicad, LTSpice, Cadence, Mentor, Altium or Visio, why is FidoCadJ interesting?");
define("FAQ_CONTENT_7","<p>Because it is a <b>different program</b> pursuing different purposes. It is complementary with the big EDA electronic tools. Ever tried to include your schematics in a document or in a presentation? Were you happy of the result?</p>
<p>If you want to publish and share your drawings and you are not interested in the netlist features and simulation, FidoCadJ may be the tool for you. It is LaTeX-friendly: you can export drawings in a PGF/TikZ script to be included in your document.</p>");

define("FAQ_TITLE_8","FidoCadJ does not run or is very slow, what can I do?");
define("FAQ_CONTENT_8",'<p>Most of the times, if you can not run FidoCadJ in your system, this means that Java is not correctly installed. If FidoCadJ runs, but is abnormally slow, this might indicate that the configuration of Java is not optimal.</p>
<p>It happened in the past with some Linux distributions, that suboptimal graphic drivers gave disappointing rendering performance with Java. If other Java applications run perfectly fine, <a href="https://sourceforge.net/p/fidocadj/discussion/?source=navbar">contact us on the forum</a>.</p>');

define("FAQ_TITLE_9","Version number is 0.something. Is it an unstable/unfinished project?");
define("FAQ_CONTENT_9","<p>FidoCadJ is nowadays quite <b>mature</b>. The fact that version number begins with a zero has nothing to do with that. It is just a convention. Unstable/developer versions are instead followed by a greek letter.</p>
<p>You can try them, but you are warned that some bugs might sneak here and there.</p>");

define("FAQ_TITLE_10","How can I participate to the development?");
define("FAQ_CONTENT_10",'<p>FidoCadJ is an <b>open source</b> project. You can freely have a look at the <a href="https://github.com/DarwinNE/FidoCadJ">complete source code in the GitHub repository</a>. Also this website is kept in the repository.</p>
<p>You can review the code, spot bugs, suggest improvements in the application or in the documentation. Becoming an active contributor, with write access on the repository, requires you to read the <a href="https://github.com/DarwinNE/FidoCadJ/blob/master/README">README</a> file, and discuss a little with the other developers using <a href="https://github.com/DarwinNE/FidoCadJ/issues">Issues</a>.</p>');
 ?>

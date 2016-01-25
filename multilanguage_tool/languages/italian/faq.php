<?php
//this file contains the definitions of page download.php

//description attribute of the download page
define("FAQ_PAGE_DESCRIPTION", "Domande frequenti su FidoCadJ e su come funziona. Come scaricarlo gratuitamente e usare il software gratuito per disegnare e condividere i tuoi progetti.");

//title attribute of the download page
define("TITLE_PAGE_DESCRIPTION", "FidoCadJ Domande Frequenti");

define("PAGE_SUB_TITLE","Domande Frequenti");

//FAQ INDEX
define("FAQ_INDEX_1","Cos'è FidoCadJ?");
define("FAQ_INDEX_2","Posso usare FidoCadJ per altro, oltre all'elettronica?");
define("FAQ_INDEX_3","FidoCadJ è gratuito?");
define("FAQ_INDEX_4","Quali sono i requisiti di sistema per utilizzare FidoCadJ?");
define("FAQ_INDEX_5","Come si ruotano o specchiano i simboli?");
define("FAQ_INDEX_6","Perché usare FidoCadJ per il mio sito o il mio forum?");
define("FAQ_INDEX_7","Uso già Kicad, LTSpice, Cadence, Mentor, Altium or Visio, perché dovrei interessarmi a FidoCadJ?");
define("FAQ_INDEX_8","FidoCadJ non si avvia oppure è troppo lento, come posso risolvere?");
define("FAQ_INDEX_9","Il numero della versione è 0.qualcosa. È un progetto instabile o incompleto?");
define("FAQ_INDEX_10","Come posso partecipare allo sviluppo?");

//FAQ
define("FAQ_TITLE_1","Cos'è FidoCadJ?");
define("FAQ_CONTENT_1","<p>FidoCadJ è un <b>semplice</b> editor per grafica vettoriale. Puoi disegnarci qualsiasi cosa e FidoCadJ include una vasta libreria di simboli di elettronica ed elettrotecnica.</p>");

define("FAQ_TITLE_2","Posso usare FidoCadJ per altro, oltre all'elettronica?");
define("FAQ_CONTENT_2",'<p>Si, certamente! FidoCadJ può essere utilizzato per <a href="http://www.matematicamente.it/forum/viewtopic.php?f=38&t=114624">diagrammi</a>, <a href="http://www.electroyou.it/pepito/wiki/libreria-flowchart-per-fidocadj">flow-chart</a> e <a href="http://www.electroyou.it/admin/wiki/peanuts-fidocadj">perfino vignette</a>. Nelle <a href="http://darwinne.github.io/FidoCadJ/scrn.html">schermate</a> trovi qualche esempio.</p>');

define("FAQ_TITLE_3","FidoCadJ è gratuito?");
define("FAQ_CONTENT_3","<p>Si. FidoCadJ è un <b>Software Libero</b>, rilasciato sotto General Public License version 3.</p>");

define("FAQ_TITLE_4","Quali sono i requisiti di sistema per utilizzare FidoCadJ?");
define("FAQ_CONTENT_4","<p>Hai bisogno di un computer con Java ".JAVA_VERSION_REQUIRED." installato. Qualsiasi sistema operativo (Windows, Linux, MacOSX,...). È disponibile anche una versione di FidoCadJ per tablet o cellulari Android (necessaria almeno la versione 4.0).</p>");

define("FAQ_TITLE_5","Come si ruotano o specchiano i simboli?");
define("FAQ_CONTENT_5","<p>Premi il tasto R oppure S mentre stai modificando o posizionando il simbolo. Puoi anche utilizzare le voci Ruota/Specchia nel menù Modifica.</p>");

define("FAQ_TITLE_6","Perché usare FidoCadJ per il mio sito o il mio forum?");
define("FAQ_CONTENT_6","<p>Perché è possibile condividere semplicemente i sorgenti dei disegni. FidoCadJ utilizza un accessibile formato testo per i suoi file, dettagliatamente descritto nel <a href='https://github.com/DarwinNE/FidoCadJ/releases/download/v0.24.5/manual_en.pdf'>manuale</a>. I tuoi utenti possono prelevarlo, modificare il disegno come preferiscono e caricarlo nuovamente per discutere le variazioni.</p>");

define("FAQ_TITLE_7","Uso già Kicad, LTSpice, Cadence, Mentor, Altium or Visio, perché dovrei interessarmi a FidoCadJ?");
define("FAQ_CONTENT_7","<p>Perché è un <b>programma differente</b> che persegue differenti propositi. È complementare ai grandi software elettronici EDA. Hai mai provato ad includere uno schema in un documento oppure in una presentazione? Sei stato soddisfatto dai risultati?</p>
<p>Se ti interessa pubblicare e condividere i tuoi disegni, e non sei interessato alle funzionalità di simulazione e netlist, FidoCadJ è quello che ti serve. È compatibile con LaTeX: puoi esportare i tuoi elaborati in uno script PGF/TikZ da includere nel documento.</p>");

define("FAQ_TITLE_8","FidoCadJ non si avvia oppure è troppo lento, come posso risolvere?");
define("FAQ_CONTENT_8",'<p>Per la maggior parte dei casi, se FidoCadJ non lavora bene sul tuo sistema, significa che Java non è installato correttamente. Se FidoCadJ si avvia, ma è davvero molto lento, questo potrebbe indicare che la configurazione di Java non è ottimale.</p>
<p>È successo qualche volta in alcune distribuzioni Linux che i driver grafici elaboravano malamente le operazioni di rendering. Se invece altre applicazioni Java lavorano bene sul tuo sistema, <a href="https://sourceforge.net/p/fidocadj/discussion/?source=navbar">contattaci</a> e saremo felici di aiutarti.</p>');

define("FAQ_TITLE_9","Il numero della versione è 0.qualcosa. È un progetto instabile o incompleto?");
define("FAQ_CONTENT_9","<p>FidoCadJ è ormai abbastanza <b>maturo</b>. Il fatto che la versione sia numerata partendo da zero è solo una convenzione per gli sviluppatori. Le versioni in sviluppo e ancora instabili sono invece seguite da una lettera greca: alfa, beta ecc...</p>
<p>Puoi provarle, ma potresti incontrare qualche piccolo malfunzionamento.</p>");

define("FAQ_TITLE_10","Come posso partecipare allo sviluppo?");
define("FAQ_CONTENT_10",'<p>FidoCadJ è un progetto <b>open source</b>. Puoi liberamente accedere al <a href="https://github.com/DarwinNE/FidoCadJ">codice sorgente completo nel repository su GitHub</a>. Anche questo sito è ospitato nel repository.</p>
<p>Puoi controllare il codice, segnalare bug, suggerire migliorie per l\'applicazione o per la documentazione. Per diventare un collaboratore attivo, con i permessi di scrittura nel repository, è necessario che tu legga il file <a href="https://github.com/DarwinNE/FidoCadJ/blob/master/README">README</a>, e discuta delle tue proposte con gli altri sviluppatori su <a href="https://github.com/DarwinNE/FidoCadJ/issues">Issues</a>.</p>');
 ?>

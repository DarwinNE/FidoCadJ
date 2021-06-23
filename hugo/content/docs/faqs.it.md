---
title: "FAQ"
weight: 1
# bookFlatSection: false
bookToc: true
# bookHidden: false
# bookCollapseSection: false
# bookComments: false
# bookSearchExclude: false
---

# Domande Frequenti
# Cos'è FidoCadJ?

FidoCadJ è un **semplice** editor per grafica vettoriale. Puoi disegnarci qualsiasi cosa e FidoCadJ include una vasta libreria di simboli di elettronica ed elettrotecnica.

## Posso usare FidoCadJ per altro, oltre all'elettronica?

Si, certamente! FidoCadJ può essere utilizzato per [diagrammi](https://www.matematicamente.it/forum/viewtopic.php?f=38&t=114624), [flow-chart](https://www.electroyou.it/pepito/wiki/libreria-flowchart-per-fidocadj) e [perfino vignette](https://www.electroyou.it/admin/wiki/peanuts-fidocadj). Nelle [schermate]({{< relref "/docs/screenshots" >}}) trovi qualche esempio.

## FidoCadJ è gratuito?

Si. FidoCadJ è un **Software Libero**, rilasciato sotto General Public License version 3.

## Quali sono i requisiti di sistema per utilizzare FidoCadJ?

Hai bisogno di un computer con Java 9 installato. Qualsiasi sistema operativo (Windows, Linux e macOS). È disponibile anche una versione di FidoCadJ per tablet o cellulari Android (necessaria almeno la versione 4.0).

## Come si ruotano o specchiano i simboli?

Premi il tasto R oppure S mentre stai modificando o posizionando il simbolo. Puoi anche utilizzare le voci Ruota/Specchia nel menù Modifica.

## Perché usare FidoCadJ per il mio sito o il mio forum?

Perché è possibile condividere semplicemente i sorgenti dei disegni. FidoCadJ utilizza un accessibile formato testo per i suoi file, dettagliatamente descritto nel [manuale]({{% siteparam "userManualUrl" %}}). I tuoi utenti possono prelevarlo, modificare il disegno come preferiscono e caricarlo nuovamente per discutere le variazioni.

## Uso già Kicad, LTSpice, Cadence, Mentor, Altium or Visio, perché dovrei interessarmi a FidoCadJ?

Perché è un **programma differente** che persegue differenti propositi. È complementare ai grandi software elettronici EDA. Hai mai provato ad includere uno schema in un documento oppure in una presentazione? Sei stato soddisfatto dai risultati?

Se ti interessa pubblicare e condividere i tuoi disegni, e non sei interessato alle funzionalità di simulazione e netlist, FidoCadJ è quello che ti serve. È compatibile con LaTeX: puoi esportare i tuoi elaborati in uno script PGF/TikZ da includere nel documento.

## FidoCadJ non si avvia oppure è troppo lento, come posso risolvere?

Per la maggior parte dei casi, se FidoCadJ non lavora bene sul tuo sistema, significa che Java non è installato correttamente. Se FidoCadJ si avvia, ma è davvero molto lento, questo potrebbe indicare che la configurazione di Java non è ottimale.

È successo qualche volta in alcune distribuzioni Linux che i driver grafici elaboravano malamente le operazioni di rendering. Se invece altre applicazioni Java lavorano bene sul tuo sistema, [contattaci](https://github.com/DarwinNE/FidoCadJ/issues) e saremo felici di aiutarti.

## Il numero della versione è 0.qualcosa. È un progetto instabile o incompleto?

FidoCadJ è ormai abbastanza **maturo**. Il fatto che la versione sia numerata partendo da zero è solo una convenzione per gli sviluppatori. Le versioni in sviluppo e ancora instabili sono invece seguite da una lettera greca: alfa, beta ecc...

Puoi provarle, ma potresti incontrare qualche piccolo malfunzionamento.

## Come posso partecipare allo sviluppo?
FidoCadJ è un progetto **open source**. Puoi liberamente accedere al [codice sorgente completo nel repository su GitHub](https://github.com/DarwinNE/FidoCadJ). Anche questo sito è ospitato nel repository.

Puoi controllare il codice, segnalare bug, suggerire migliorie per l'applicazione o per la documentazione. Per diventare un collaboratore attivo, con i permessi di scrittura nel repository, è necessario che tu legga il file [README](https://github.com/DarwinNE/FidoCadJ/blob/master/README), e discuta delle tue proposte con gli altri sviluppatori su [Issues](https://github.com/DarwinNE/FidoCadJ/issues).

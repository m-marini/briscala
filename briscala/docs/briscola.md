Briscola
========

Stato
-----

Le carte da gioco sono 40: 10 carte di briscola e 10 carte di 3 gruppi di altri semi

Lo stato di gioco è determinato da:
  * una sequenza di carte in mano al giocatore
  * un insieme di carte vinte dal giocatore
  * un insieme di carte perse dal giocatore
  * la carte di briscola
  * la carta giocata dall'avversario (facoltativa)
  * una sequenza di carte rimanenti (quelle nel mazzo + quelle dell'avversario)

Ogni passo di gioco consinte nella scelta da parte del giocatore di una delle proprie carte.
Abbiamo vari possibilità dello stato successivo:

  * fine il giocatore non ha più carte
  * finale sono rimaste al più 3 carte nel mazzo e nessuna briscola
  * finale sono rimaste al più 2 carte nel mazzo e la briscola
  * durante il gioco: sono rimaste più di 3 carte nel mazzo e nessuna carta giocata
  * durante il gioco: sono rimaste più di 3 carte nel mazzo e una carta giocata


Piano di sviluppo
-----------------

### Fase iniziale

Dalle partite generate si deve estrarre i reward e i relativi ritorni scontati.

Il reward sarà
  *  1 quando il giocatore vince (punteggio > 60)
  * -1 quando il giocatore perde (punteggio avversario > 60)
  * 0 in utti gli altri casi

Il valore di sconto è tale che dopo 20 giocate (durata del gioco) il valore sarà dimezzato quindi
    `gamma ^ 20 = 0.5`
    `gamma = 0.5 ^ 1/20 ~= 0.965936`

Dobbiamo dividere ogni partita in due episodi separati uno per giocatore prendendo solo i passi (giocate)
effettuate da un giocatore e i ritorni calcolati alla mano di gioco sucessiva.

Dividere il sample set in 3 gruppi:

  1 Il training set 60% usato per imparare
  2 il validation set 20% usato per regolare i parametri di apprendimento 
  3 il test set 20% usato per misurare l'efficacia

Calcolare la funzione azione-valore Q(s, a) determinata dai campioni in esame usando varie rete neurali
e selezionare la migliore su validation set.

Calcolare l'efficacia della rete neurale scelta sul test set.




### TD Learning

Con la rete neurale selezionate applicare gli algoritmi di TD-learning (Sarsa - Q-Learning)
per miglirare l'efficacia della rete.





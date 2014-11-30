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


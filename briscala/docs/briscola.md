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

Dalle partite generate si deve estrarre i reward e i relativi ritorni
scontati.

Il reward sarà
  *  1 quando il giocatore vince (punteggio > 60)
  * -1 quando il giocatore perde (punteggio avversario > 60)
  * 0 in utti gli altri casi

Il valore di sconto è tale che dopo 20 giocate (durata del gioco) il
valore sarà dimezzato quindi
    `gamma ^ 20 = 0.5`
    `gamma = 0.5 ^ 1/20 ~= 0.965936`

Dobbiamo dividere ogni partita in due episodi separati uno per giocatore
prendendo solo i passi (giocate) effettuate da un giocatore e i ritorni
calcolati alla mano di gioco sucessiva.

Dividere il sample set in 3 gruppi:

  1 Il training set 60% usato per imparare
  2 il validation set 20% usato per regolare i parametri di apprendimento 
  3 il test set 20% usato per misurare l'efficacia

Calcolare la funzione azione-valore Q(s, a) determinata dai campioni
in esame usando varie rete neurali e selezionare la migliore su
validation set.

Calcolare l'efficacia della rete neurale scelta sul test set.




### TD Learning

Con la rete neurale selezionate applicare gli algoritmi di TD-learning
 (Sarsa - Q-Learning)
per miglirare l'efficacia della rete.


### Funzione di valore di stato-azione `Q(s, a)`

La funzione `Q(s, a)` indica la probabilità di vincita se nello stato
`s` si effettua l'azione `a` (giocata).
La stratega migliore di gioco sarà quindi quella di giocare l'azione
con maggior valore di `Q(s, a)`.

        a = a' | max Q(s, a'), per ogni a' ammesso in s

Il giocatore che ha la mano sa già quale sarà lo stato sucessivo (carta giocata + turno all'avversario).
In questo caso possiamo usare gli afterstate e quindi la policy sarà
quella di valutare il valore degli afterstate e non di `Q(S, a)`

Gli stati finali dove i giocatori hanno una sola carta da giocare non c'è
alcuna scelta da fare per cui possiamo ingnorarli considerando solo lo stato
finale di gioco come afterstate dell'ultima scelta fatta.

        S(m)	                         = AS(m) per il giocatore a
        S(m-1, giocatore ? con 1 carta)  = ignorato
        S(m-2, giocatore ? con 1 carta)  = ignorato
        S(m-3, giocatore b con 2 carte, nessuna carta in tavola)

Per stati dove il giocatore è di prima mano (non c'è nessuna carta in tavola)
lo stato sucessivo è determinato esclusivamente dall'azione giocata e quindi
possiamo usare lo stato sucessivo come after state:

        S(m, giocatore a con più di una carta, carta in tavola) = AS(m) per il giocatore b
        S(m-1, giocatore b con più di una carta, nessuna carta in tavola)

Per stati dove il giocatore risponde (c'è una carta in tavola)
lo stato sucessivo è determinato dall'azione giocata e dalle carte pescate

        S(m, giocatore a con più di una carta, carta in tavola) = Q(S(m), A(m)) per il giocatore a

Come si vede gli stati dove il giocatore è di prima mano non contribuiscono alla
definizione della policy.




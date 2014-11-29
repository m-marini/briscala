Briscola
========

Stato
-----

Lo stato di gioco della briscola è espresso con 40 carte.

Le prime 10 rappresentano le briscole e possono avere 5 stati esclusivi:
  
  1 in mano al giocatore
  2 in mano all'avversario
  3 nel mazzo del giocatore
  4 nel mazzo dell'avversario
  5 nel mazzo

e uno stato aggiuntivo
  6 scoperta

Le altre carte possono avere 5 possibili stati
  
  1 in mano al giocatore
  2 in mano all'avversario
  3 nel mazzo del giocatore
  4 nel mazzo dell'avversario
  5 nel mazzo

Possiamo quindi identificare

`10 * 6 + 30 * 5 = 60 + 150`

210 features binarie delle quali solo 10 + 1 + 30 = 41 attive.

I semi non briscola sono tra loro intercambiabili per rappresentare lo stato.
Per ridurre gli stati possibili i semi delle 30 carte sono ordinate per

  1 # di carte nel mazzo,
  2 # di carte nel mazzo del giocatore,
  3 # di carte nel mazzo avversario.

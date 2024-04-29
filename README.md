Aplikacja realizuje funkcjonalność listy zadań do wykonania. Użytkownik ma możliwość tworzenia,
usuwania i edycji zadań zgodnie z poniższymi wymaganiami. Utworzone zadania przechowywane są w
bazie danych w pamięci urządzenia. Aplikacja informuje o zbliżającym się terminie wykonania zadania
za pomocą powiadomień.
Wymagania:
• Lista przewijana do wyświetlania zadań (w oparciu o klasę RecyclerView)
• Tworzenie zadań (za pomocą Floating Action Button)
• Zadanie zawiera:
o Tytuł
o Opis
o Czas utworzenia
o Czas wykonania
o Status - zakończone / niezakończone
o Powiadomienie (włączone/wyłączone)
o Kategoria
o Załączniki (zdjęcie, plik) + znacznik na liście zadań, że zadanie zawiera załączniki
• Ustawienia aplikacji
o Opcja do ukrywania zakończonych zadań
o Wybór kategorii zadań widocznych na liście
o Czas powiadomień - ile minut przed czasem wykonania powinno być wyświetlone
powiadomienie

• Wyświetlanie szczegółów / edycja zadań
• Sortowanie zadań na liście po czasie wykonania (najbardziej pilne na górze)
• Opcja wyszukiwania zadań (pole wyszukiwania nad listą)
• Przechowywanie zadań w bazie danych (SQLite)
• Powiadomienia
• Przechowywanie załączników w pamięci zewnętrznej aplikacji


Metody:
- Widget kalendarza
- Checkbox przy tasku
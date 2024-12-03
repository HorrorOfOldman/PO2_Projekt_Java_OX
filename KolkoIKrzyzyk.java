import java.util.Scanner;

public class KolkoIKrzyzyk {

    public static void main(String[] args) {
        char[][] plansza = new char[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                plansza[i][j] = ' ';  // Inicjalizacja pustych pól
            }
        }

        char aktualnyGracz = 'X';  // Rozpoczyna gracz X
        boolean graWTrwa = true;
        Scanner scanner = new Scanner(System.in);

        while (graWTrwa) {
            wyswietlPlansze(plansza);
            System.out.println("Gracz " + aktualnyGracz + ", podaj wiersz i kolumnę (0-2): ");
            int wiersz = scanner.nextInt();
            int kolumna = scanner.nextInt();

            // Sprawdzenie, czy pole jest puste
            if (wiersz >= 0 && wiersz < 3 && kolumna >= 0 && kolumna < 3 && plansza[wiersz][kolumna] == ' ') {
                plansza[wiersz][kolumna] = aktualnyGracz;
            } else {
                System.out.println("Niepoprawny ruch, spróbuj ponownie.");
                continue;
            }

            // Sprawdzenie, czy ktoś wygrał
            if (czyWygrana(plansza, aktualnyGracz)) {
                wyswietlPlansze(plansza);
                System.out.println("Gratulacje! Gracz " + aktualnyGracz + " wygrał!");
                graWTrwa = false;
            } else if (czyRemis(plansza)) {
                wyswietlPlansze(plansza);
                System.out.println("Remis!");
                graWTrwa = false;
            } else {
                // Zmiana gracza
                aktualnyGracz = (aktualnyGracz == 'X') ? 'O' : 'X';
            }
        }

        scanner.close();
    }

    // Funkcja do wyświetlania planszy
    public static void wyswietlPlansze(char[][] plansza) {
        System.out.println("-----------");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print("| " + plansza[i][j] + " ");
            }
            System.out.println("|");
            System.out.println("-----------");
        }
    }

    // Funkcja sprawdzająca, czy dany gracz wygrał
    public static boolean czyWygrana(char[][] plansza, char gracz) {
        // Sprawdzenie wierszy, kolumn i przekątnych
        for (int i = 0; i < 3; i++) {
            if (plansza[i][0] == gracz && plansza[i][1] == gracz && plansza[i][2] == gracz)
                return true;
            if (plansza[0][i] == gracz && plansza[1][i] == gracz && plansza[2][i] == gracz)
                return true;
        }
        if (plansza[0][0] == gracz && plansza[1][1] == gracz && plansza[2][2] == gracz)
            return true;
        if (plansza[0][2] == gracz && plansza[1][1] == gracz && plansza[2][0] == gracz)
            return true;

        return false;
    }

    // Funkcja sprawdzająca, czy gra zakończyła się remisem
    public static boolean czyRemis(char[][] plansza) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (plansza[i][j] == ' ') {
                    return false;  // Gra nie zakończona, są jeszcze wolne pola
                }
            }
        }
        return true;  // Brak wolnych pól, remis
    }
}

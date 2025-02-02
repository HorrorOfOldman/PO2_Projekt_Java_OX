import java.io.*;
import java.net.*;

public class KolkoKrzyzykServer {
    private static final int PORT = 12345;
    private static char[][] board = new char[3][3];
    private static PrintWriter outPlayer1, outPlayer2;
    private static BufferedReader inPlayer1, inPlayer2;
    private static char currentPlayerMark = 'X';

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serwer uruchomiony na porcie " + PORT);

            // Akceptowanie graczy
            Socket player1 = serverSocket.accept();
            System.out.println("Gracz 1 połączony.");
            outPlayer1 = new PrintWriter(player1.getOutputStream(), true);
            inPlayer1 = new BufferedReader(new InputStreamReader(player1.getInputStream()));

            Socket player2 = serverSocket.accept();
            System.out.println("Gracz 2 połączony.");
            outPlayer2 = new PrintWriter(player2.getOutputStream(), true);
            inPlayer2 = new BufferedReader(new InputStreamReader(player2.getInputStream()));

            // Inicjalizacja planszy
            resetBoard();

            // Wysłanie informacji startowych
            outPlayer1.println("START:X");
            outPlayer2.println("START:O");
            outPlayer1.println("YOUR_TURN");
            System.out.println("Plansza została zresetowana.");
            System.out.println("Wysłano do gracza 1: START:X");
            System.out.println("Wysłano do gracza 2: START:O");

            // Obsługa gry
            handleGame();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void resetBoard() {//resetowanie planszy
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }

    private static void handleGame() throws IOException {
        boolean gameRunning = true;

        while (gameRunning) {
            if (currentPlayerMark == 'X') {
                gameRunning = handlePlayerMove(inPlayer1, outPlayer1, outPlayer2, 'X');
            } else {
                gameRunning = handlePlayerMove(inPlayer2, outPlayer2, outPlayer1, 'O');
            }
        }
    }

    private static boolean handlePlayerMove(BufferedReader in, PrintWriter out, PrintWriter opponentOut, char playerMark) throws IOException {
        out.println("YOUR_TURN");
        System.out.println("Gracz " + playerMark + " wykonuje ruch.");

        // Odbieranie ruchu
        String move = in.readLine();
        if (move == null) {
            System.out.println("Gracz " + playerMark + " rozłączył się.");
            return false;
        }

        System.out.println("Otrzymano ruch od gracza " + playerMark + ": " + move);

        // Parsowanie ruchu
        try {
            String[] parts = move.split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);

            // Sprawdzanie poprawności ruchu
            if (board[row][col] == '-') {
                board[row][col] = playerMark;
                out.println("VALID_MOVE");
                opponentOut.println("OPPONENT_MOVE:" + row + "," + col);
                System.out.println("Ruch zaakceptowany i wysłany do przeciwnika.");

                // Sprawdzanie warunków zakończenia gry
                if (checkWin(playerMark)) {
                    out.println("YOU_WIN");
                    opponentOut.println("YOU_LOSE");
                    System.out.println("Gracz " + playerMark + " wygrał!");
                    return false;
                } else if (isBoardFull()) {
                    out.println("DRAW");
                    opponentOut.println("DRAW");
                    System.out.println("Gra zakończyła się remisem.");
                    return false;
                }

                // Zmiana gracza
                currentPlayerMark = (playerMark == 'X') ? 'O' : 'X';
            } else {
                out.println("INVALID_MOVE");
                System.out.println("Gracz " + playerMark + " wykonał nieprawidłowy ruch.");
            }
        } catch (Exception e) {
            System.out.println("Błąd podczas przetwarzania ruchu: " + e.getMessage());
            out.println("INVALID_MOVE");
        }

        return true;
    }

    private static boolean checkWin(char playerMark) {
        // Sprawdzanie wierszy, kolumn i przekątnych
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == playerMark && board[i][1] == playerMark && board[i][2] == playerMark) return true;
            if (board[0][i] == playerMark && board[1][i] == playerMark && board[2][i] == playerMark) return true;
        }
        if (board[0][0] == playerMark && board[1][1] == playerMark && board[2][2] == playerMark) return true;
        if (board[0][2] == playerMark && board[1][1] == playerMark && board[2][0] == playerMark) return true;
        return false;
    }

    private static boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') return false;
            }
        }
        return true;
    }
}

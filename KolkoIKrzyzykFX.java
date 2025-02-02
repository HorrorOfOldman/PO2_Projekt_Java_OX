package com.example.kolko_krzyzyk.KolkoIKrzyzykFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class KolkoIKrzyzykFX extends Application {

    private Button[][] buttons = new Button[3][3]; // Przyciski reprezentujące planszę
    private TextArea messages = new TextArea(); // Pole do wyświetlania komunikatów
    private PrintWriter out;
    private BufferedReader in;
    private String playerMark; // Znak gracza (X lub O)
    private boolean myTurn = false; // Czy jest tura gracza?
    private String message;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Połączenie z serwerem
        connectToServer();

        // Ustawienie GUI
        VBox root = new VBox();
        GridPane grid = new GridPane();
        messages.setEditable(false);

        // Tworzenie siatki przycisków
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Button button = new Button(" ");
                button.setPrefSize(100, 100);
                int finalRow = row;
                int finalCol = col;

                button.setOnAction(e -> handleMove(finalRow, finalCol, button));
                buttons[row][col] = button;
                grid.add(button, col, row);
            }
        }

        root.getChildren().addAll(new Label("Kółko-krzyżyk"), grid, messages);

        Scene scene = new Scene(root, 400, 500);
        primaryStage.setTitle("Kółko-krzyżyk - Klient");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Odbieranie komunikatów od serwera
        new Thread(this::receiveMessages).start();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("192.168.1.1", 12345); // Adres serwera
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void handleMove(int row, int col, Button button) {
        if (!myTurn || !button.getText().equals(" ")) {
            messages.appendText("Nie możesz teraz wykonać ruchu!\n");
            return;
        }

        // Wysłanie ruchu do serwera
        out.println(row + "," + col);
        button.setText(playerMark); // Aktualizacja przycisku
        myTurn = false; // Czekanie na ruch przeciwnika
    }

    private void receiveMessages() {

        try {
            while (true) {
                String message = in.readLine();

                Platform.runLater(() -> {
                    if (message.startsWith("START")) {
                        playerMark = message.split(":")[1];
                        messages.appendText("Twój znak: " + playerMark + "\n");
                    } else if (message.startsWith("YOUR_TURN")) {
                        myTurn = true;
                        messages.appendText("Twoja kolej!\n");
                    } else if (message.startsWith("OPPONENT_MOVE")) {
                        String[] parts = message.split(":")[1].split(",");
                        int row = Integer.parseInt(parts[0]);
                        int col = Integer.parseInt(parts[1]);

                        // Aktualizacja planszy
                        String opponentMark = playerMark.equals("X") ? "O" : "X";
                        buttons[row][col].setText(opponentMark);
                        messages.appendText("Ruch przeciwnika: " + row + "," + col + "\n");
                    } else if (message.equals("YOU_WIN")) {
                        messages.appendText("Wygrałeś!\n");
                        disableBoard();
                    } else if (message.equals("YOU_LOSE")) {
                        messages.appendText("Przegrałeś!\n");
                        disableBoard();
                    } else if (message.equals("DRAW")) {
                        messages.appendText("Remis!\n");
                        disableBoard();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message.equals("RESET_GAME")) {
            resetGame();
        }

    }


    private void disableBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setDisable(true);
            }
        }
    }
    private void resetGame() {
        Platform.runLater(() -> {
            // Czyścimy planszę
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setText("");
                    buttons[i][j].setDisable(false);
                }
            }

            // Zamiana gracza rozpoczynającego
            playerMark = playerMark.equals("X") ? "O" : "X";
            myTurn = playerMark.equals("X"); // X zawsze zaczyna

            // Czyścimy pole wiadomości
            messages.clear();
            messages.appendText("Nowa runda! Gracz " + playerMark + " zaczyna.\n");

            // Informujemy serwer o nowej rundzie
            out.println("RESET");
        });
    }

}

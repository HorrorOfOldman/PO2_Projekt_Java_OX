package com.example.kolko_krzyzyk.KolkoIKrzyzykFX;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class KolkoIKrzyzykFX extends Application {

    private char[][] plansza = new char[3][3];
    private char aktualnyGracz = 'X'; // Zaczyna gracz X

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-background-color: lightblue; -fx-padding: 10;");
        grid.setHgap(5);
        grid.setVgap(5);

        // Inicjalizacja planszy
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                plansza[i][j] = ' '; // Puste pola
                StackPane pole = stworzPole(i, j);
                grid.add(pole, j, i);
            }
        }

        Scene scene = new Scene(grid, 300, 300);
        stage.setScene(scene);
        stage.setTitle("Kółko i Krzyżyk");
        stage.show();
    }

    // Tworzenie jednego pola na planszy
    private StackPane stworzPole(int wiersz, int kolumna) {
        Rectangle rect = new Rectangle(80, 80);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.BLACK);

        Text text = new Text();
        text.setFont(Font.font(36));

        StackPane pole = new StackPane(rect, text);
        pole.setOnMouseClicked(event -> {
            if (plansza[wiersz][kolumna] == ' ') {
                plansza[wiersz][kolumna] = aktualnyGracz;
                text.setText(String.valueOf(aktualnyGracz));
                if (czyWygrana(aktualnyGracz)) {
                    pokazWynik("Gracz " + aktualnyGracz + " wygrał!");
                    resetujGre();
                } else if (czyRemis()) {
                    pokazWynik("Remis!");
                    resetujGre();
                } else {
                    aktualnyGracz = (aktualnyGracz == 'X') ? 'O' : 'X';
                }
            }
        });

        return pole;
    }

    // Sprawdzenie, czy dany gracz wygrał
    private boolean czyWygrana(char gracz) {
        for (int i = 0; i < 3; i++) {
            if (plansza[i][0] == gracz && plansza[i][1] == gracz && plansza[i][2] == gracz) return true;
            if (plansza[0][i] == gracz && plansza[1][i] == gracz && plansza[2][i] == gracz) return true;
        }
        if (plansza[0][0] == gracz && plansza[1][1] == gracz && plansza[2][2] == gracz) return true;
        if (plansza[0][2] == gracz && plansza[1][1] == gracz && plansza[2][0] == gracz) return true;

        return false;
    }

    // Sprawdzenie, czy gra zakończyła się remisem
    private boolean czyRemis() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (plansza[i][j] == ' ') return false; // Są jeszcze wolne pola
            }
        }
        return true;
    }

    // Wyświetlenie wyniku gry
    private void pokazWynik(String wynik) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Koniec gry");
        alert.setHeaderText(null);
        alert.setContentText(wynik);
        alert.showAndWait();
    }

    // Resetowanie gry
    private void resetujGre() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                plansza[i][j] = ' ';
            }
        }
        start(new Stage());
    }
}

package com.example.nhom4ai.codettnt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class PuzzleApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PuzzleApplication.class.getResource("puzzle.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 850);
        stage.setTitle("Puzzle");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
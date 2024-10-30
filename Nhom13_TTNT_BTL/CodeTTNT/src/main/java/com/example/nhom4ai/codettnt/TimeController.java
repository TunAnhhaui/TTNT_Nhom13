package com.example.nhom4ai.codettnt;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TimeController {
    @FXML
    private Label timerLabel;
    @FXML
    private Button startButton; // Reference to the start button for possible UI updates

    private int seconds = 0;
    private Timeline timeline;

    // Method called when Start Timer button is clicked
    @FXML
    private void startTimer() {
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
            timeline.stop(); // Stops and resets if already running
            seconds = 0;     // Reset time
            timerLabel.setText("00:00"); // Reset displayed time
        }

        // Timeline setup for timer
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds++;
            timerLabel.setText(formatTime(seconds));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Loop indefinitely
        timeline.play(); // Start the timer
    }

    // Format time in minutes:seconds
    private String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // Stops timer when application is closed
    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
    }
}

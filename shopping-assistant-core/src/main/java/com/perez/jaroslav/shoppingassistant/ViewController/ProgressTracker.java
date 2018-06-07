package com.perez.jaroslav.shoppingassistant.ViewController;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

public class ProgressTracker {

    final int N_SECS = 10;

    public Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            public Void call() {
                for (; ; ) {
                    if (isCancelled()) {
                        break;
                    }
                    // uncomment updateProgress call if you want to show progress
                    // rather than let progress remain indeterminate.
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return null;
                    }
                }
                updateProgress(N_SECS, N_SECS);

                return null;
            }
        };
    }

    public HBox createLayout(Task task) {
        HBox layout = new HBox(10);

        layout.getChildren().setAll(
                createProgressIndicator(task)
        );

        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        return layout;
    }

    private ProgressIndicator createProgressIndicator(Task task) {
        ProgressIndicator progress = new ProgressIndicator();

        progress.progressProperty().bind(task.progressProperty());

        return progress;
    }

    private Label createCounter(Task task) {
        Label counter = new Label();

        counter.setMinWidth(20);
        counter.setAlignment(Pos.CENTER_RIGHT);
        counter.textProperty().bind(task.messageProperty());
        counter.setStyle("-fx-border-color: forestgreen;");

        return counter;
    }
}

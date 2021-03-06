package com.perez.jaroslav.shoppingassistant;

import com.perez.jaroslav.shoppingassistant.ViewController.MainController;
import com.perez.jaroslav.shoppingassistant.weight.WeightManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ShoppingAssistant extends Application {

    private static WeightManager weightManager = WeightManager.getInstance();
    private static final String MAIN_PANE_FXML = "/MainPane.fxml";
    private MainController mainController;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(MAIN_PANE_FXML));
        BorderPane root = (BorderPane) fxmlLoader.load();
        mainController = (MainController) fxmlLoader.getController();
        primaryStage.setTitle("Shopping Assistant");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        List<String> credentials = getParameters().getUnnamed();

        weightManager.initAllegroApi(credentials.get(0), credentials.get(1), credentials.get(2));
        /*getResourceFiles("/").stream().forEach(p -> {
            System.out.println(p);
        });*/
    }

    @Override
    public void stop() {
        System.out.println("STOP");
        mainController.stopThread();
    }


    private List<String> getResourceFiles(String path) throws IOException {
        List<String> filenames = new ArrayList<>();
        try (
                InputStream in = getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String resource;

            while ((resource = br.readLine()) != null) {
                filenames.add(resource);
            }
        }

        return filenames;
    }

    private InputStream getResourceAsStream(String resource) {
        final InputStream in
                = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}

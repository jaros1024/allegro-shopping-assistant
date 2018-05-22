package com.perez.jaroslav.shoppingassistant;

import com.perez.jaroslav.allegrosearchapi.AllegroApi;
import com.perez.jaroslav.allegrosearchapi.filters.Filter;
import com.perez.jaroslav.allegrosearchapi.filters.FilterOption;
import com.perez.jaroslav.allegrosearchapi.filters.InputFilter;
import com.perez.jaroslav.allegrosearchapi.filters.SelectFilter;
import com.perez.jaroslav.shoppingassistant.weight.Alternative;
import com.perez.jaroslav.shoppingassistant.weight.InputAlternative;
import com.perez.jaroslav.shoppingassistant.weight.SelectAlternative;
import com.perez.jaroslav.shoppingassistant.weight.WeightManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(MAIN_PANE_FXML));
        primaryStage.setTitle("Shopping Assistant");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        List<String> credentials=getParameters().getUnnamed();


        weightManager.initAllegroApi(credentials.get(0),credentials.get(1),credentials.get(2));
       /* getResourceFiles("/").stream().forEach(p -> {
            System.out.println(p);
        });*/
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

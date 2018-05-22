package com.perez.jaroslav.shoppingassistant.ViewController;

import com.perez.jaroslav.shoppingassistant.weight.Alternative;
import com.perez.jaroslav.shoppingassistant.weight.InputAlternative;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RadioButtonsController {
    @FXML
    private ToggleGroup myToggleGroup;
    @FXML
    private VBox vbox;
    @FXML
    private RadioButton r1;
    @FXML
    private RadioButton r2;

    private Alternative alternative;


    public RadioButtonsController(Alternative alternative,String r1Text,String r2Text) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RadioButtonsPane.fxml"));
        fxmlLoader.setController(this);
        try {
            Parent parent = (Parent) fxmlLoader.load();
            Scene scene = new Scene(parent, 400.0, 500.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.alternative=alternative;
        myToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                alternative.setName(newValue.toString());
            }
        });
        r1.setText(r1Text);
        r2.setText(r2Text);
    }

    public boolean correctData(){
        return alternative!=null;
    }
    public VBox getVbox() {
        return vbox;
    }
}

package com.perez.jaroslav.shoppingassistant.ViewController;

import com.perez.jaroslav.shoppingassistant.weight.InputAlternative;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class InputPaneController {

    @FXML
    private Label label;
    @FXML
    private TextField minimum;
    @FXML
    private TextField maximum;
    @FXML
    private VBox vbox;
    private InputAlternative alternative;

    public InputPaneController(InputAlternative alternative) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/InputPane.fxml"));
        fxmlLoader.setController(this);
        try {
            Parent parent = (Parent) fxmlLoader.load();
            Scene scene = new Scene(parent, 400.0, 500.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.alternative=alternative;
        label.setText(alternative.getName());
        minimum.setText(alternative.getMinValue());
        minimum.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*,\\d*")) {
                    String newText=newValue.replaceAll("[^(\\d|,)]", "");
                    minimum.setText(newText);
                }
                alternative.setMinValue(newValue);
            }
        });
        maximum.setText(alternative.getMaxValue());
        maximum.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*,\\d*")) {
                    String newText=newValue.replaceAll("[^(\\d|,)]", "");
                    maximum.setText(newText);
                    alternative.setMaxValue(newText);
                }
                alternative.setMaxValue(newValue);
            }
        });
    }

    public boolean correctData(){
        return alternative.getMaxValue()!=null && alternative.getMinValue()!=null && !maximum.getText().isEmpty() && !minimum.getText().isEmpty();
    }
    public VBox getVbox() {
        return vbox;
    }
}

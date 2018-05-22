package com.perez.jaroslav.shoppingassistant.ViewController;

import com.perez.jaroslav.shoppingassistant.weight.Alternative;
import com.perez.jaroslav.shoppingassistant.weight.AlternativeComparePair;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class SingleRadioController {
    @FXML
    private GridPane gridPane;
    @FXML
    private RadioButton r1;
    @FXML
    private RadioButton r2;

    private ToggleGroup group = new ToggleGroup();
    private AlternativeComparePair alternativeComparePair;

    public SingleRadioController(AlternativeComparePair alternativeComparePair) {
        this.alternativeComparePair = alternativeComparePair;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SingleRadioPane.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
            r1.setToggleGroup(group);
            r2.setToggleGroup(group);
            r1.setText(alternativeComparePair.getFirst().getName());
            r2.setText(alternativeComparePair.getSecond().getName());
           // group.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                   // alternativeComparePair.setMoreImportant(checkNewValue(((RadioButton) newValue).getText()))
          //  );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    private Alternative checkNewValue(String newValue) {
        if (alternativeComparePair.getFirst().getName().equals(newValue)) {
            return alternativeComparePair.getFirst();
        } else {
            return alternativeComparePair.getSecond();
        }
    }
}

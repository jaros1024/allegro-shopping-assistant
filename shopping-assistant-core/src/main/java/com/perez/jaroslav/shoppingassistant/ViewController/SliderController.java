package com.perez.jaroslav.shoppingassistant.ViewController;

import com.perez.jaroslav.shoppingassistant.weight.AlternativeComparePair;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class SliderController {

    @FXML
    private GridPane gridPane;
    @FXML
    private Slider slider;
    @FXML
    private Label leftLabel;
    @FXML
    private Label rightLabel;

    private AlternativeComparePair alternativeComparePair;

    public SliderController(AlternativeComparePair alternativeComparePair) {
        this.alternativeComparePair = alternativeComparePair;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SingleRadioPane.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        slider.setValue(alternativeComparePair.getMoreImportant());
        slider.valueProperty().addListener(((observable, oldValue, newValue) -> {
            int n = newValue.intValue();
            slider.setValue(newValue.intValue());
            if (n < 0)
                alternativeComparePair.setMoreImportant(1.0 / (n * -1 + 1));
            else
                alternativeComparePair.setMoreImportant(n + 1);
        }));
        leftLabel.setText(alternativeComparePair.getSecond().getName());
        rightLabel.setText(alternativeComparePair.getFirst().getName());
        GridPane.setFillWidth(rightLabel, true);
        rightLabel.setMaxWidth(Double.MAX_VALUE);
        rightLabel.setAlignment(Pos.CENTER_RIGHT);
        GridPane.setHalignment(rightLabel, HPos.RIGHT);
    }

    public GridPane getGridPane() {
        return gridPane;
    }


}

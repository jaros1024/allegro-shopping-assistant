package com.perez.jaroslav.shoppingassistant.CustomControllers;

import com.perez.jaroslav.shoppingassistant.ViewController.SingleRadioController;
import com.perez.jaroslav.shoppingassistant.ViewController.SliderController;
import com.perez.jaroslav.shoppingassistant.weight.AlternativeComparePair;
import javafx.scene.control.ListCell;
import javafx.scene.control.ToggleGroup;

public class CustomListView extends ListCell<AlternativeComparePair> {

    private ToggleGroup group = new ToggleGroup();

    @Override
    public void updateItem(AlternativeComparePair obj, boolean empty) {
        super.updateItem(obj, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            SliderController sliderController = new SliderController(obj);
            setGraphic(sliderController.getGridPane());
        }
    }


}

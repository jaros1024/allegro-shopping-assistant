package com.perez.jaroslav.shoppingassistant.ViewController;

import com.perez.jaroslav.shoppingassistant.CustomControllers.CustomListView;
import com.perez.jaroslav.shoppingassistant.weight.AlternativeComparePair;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

import java.io.IOException;
import java.util.List;

public class ChoicePaneController {

    @FXML
    private ListView listView;
    @FXML
    private ListView optionalListView;
    @FXML
    private StackPane stackPane;
    @FXML
    private CheckBox active;

    private ObservableList names = FXCollections.observableArrayList();
    private ObservableList optional=FXCollections.observableArrayList();

    public ChoicePaneController(List<AlternativeComparePair> comparePairs,List<AlternativeComparePair> op) {
        names = FXCollections.observableArrayList(comparePairs);
        optional = FXCollections.observableArrayList(op);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ChoicePane.fxml"));
        fxmlLoader.setController(this);
        try
        {
            Parent parent = (Parent)fxmlLoader.load();
            Scene scene = new Scene(parent, 400.0 ,500.0);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        active.setOnAction((event)->{
            if(active.isSelected()){
                optionalListView.setDisable(false);
            }else {
                optionalListView.setDisable(true);
            }
        });
    }

    @FXML
    void initialize() {
        listView.setItems(names);
        listView.setCellFactory(param -> new CustomListView());
        optionalListView.setItems(optional);
        optionalListView.setCellFactory(param -> new CustomListView());
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public boolean getIsActiveSelected(){
        return active.isSelected();
    }
}

package com.perez.jaroslav.shoppingassistant.ViewController;

import com.perez.jaroslav.allegrosearchapi.filters.InputFilter;
import com.perez.jaroslav.shoppingassistant.weight.Alternative;
import com.perez.jaroslav.shoppingassistant.weight.InputAlternative;
import com.perez.jaroslav.shoppingassistant.weight.SelectAlternative;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class WeightResultsController {

    @FXML
    private TreeTableView treeTable;
    @FXML
    private TreeTableColumn<Alternative, String> treeCategoryColumn;
    @FXML
    private TreeTableColumn<Alternative, String> treeWeightColumn;
    @FXML
    AnchorPane anchorPane;

    public WeightResultsController(SelectAlternative alt) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/WeightsResults.fxml"));
        fxmlLoader.setController(this);
        try {
            Parent parent = (Parent) fxmlLoader.load();
            Scene scene = new Scene(parent, 400.0, 500.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TreeItem<Alternative> treeItem = new TreeItem<>(alt);
        treeItem.setExpanded(true);
        alt.getResult().stream().forEach(p -> {
            TreeItem treeItem1 = new TreeItem(p);
            if (p instanceof SelectAlternative)
                ((SelectAlternative) p).getResult()
                        .stream()
                        .sorted((o1, o2) -> (o1.getWeight() < o2.getWeight() ? 1 : -1))
                        .forEachOrdered(p2 -> treeItem1.getChildren().add(new TreeItem<>(p2)));
            treeItem.getChildren().add(treeItem1);
        });
        treeCategoryColumn.prefWidthProperty().bind(treeTable.widthProperty().divide(2));
        treeCategoryColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Alternative, String> param) ->
                new ReadOnlyStringWrapper(param.getValue().getValue().toString()));
        treeWeightColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Alternative, String> param) ->
                new ReadOnlyStringWrapper(String.valueOf(param.getValue().getValue().getWeight())));
        treeWeightColumn.prefWidthProperty().bind(treeTable.widthProperty().divide(2));
        treeTable.setRoot(treeItem);
    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }
}

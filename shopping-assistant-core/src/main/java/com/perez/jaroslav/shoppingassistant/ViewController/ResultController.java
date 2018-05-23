package com.perez.jaroslav.shoppingassistant.ViewController;

import com.perez.jaroslav.allegrosearchapi.items.Parameter;
import com.perez.jaroslav.shoppingassistant.simplesolver.SimpleSolver;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ResultController {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TableView<SimpleSolver.Result> tableView;

    private ObservableList<SimpleSolver.Result> data = FXCollections.observableArrayList();

    public ResultController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ResultsTable.fxml"));
        fxmlLoader.setController(this);
        try {
            Parent parent = (Parent) fxmlLoader.load();
            Scene scene = new Scene(parent, 400.0, 500.0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        tableView.setEditable(false);
        TableColumn id = new TableColumn("id");
        id.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<SimpleSolver.Result, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<SimpleSolver.Result, String> param) {
                        return Bindings.createStringBinding(() -> String.valueOf(param.getValue().getItem().getId()));
                    }
                }
        );
        id.prefWidthProperty().bind(tableView.widthProperty().divide(14));
        TableColumn name = new TableColumn("nazwa");
        name.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<SimpleSolver.Result, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<SimpleSolver.Result, String> param) {
                        return Bindings.createStringBinding(() -> param.getValue().getItem().getName());
                    }
                }
        );
        name.prefWidthProperty().bind(tableView.widthProperty().divide(14));
        TableColumn weight = new TableColumn("ocena");
        weight.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<SimpleSolver.Result, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<SimpleSolver.Result, String> param) {
                        return Bindings.createStringBinding(() -> String.valueOf(param.getValue().getValue()));
                    }
                }
        );
        weight.prefWidthProperty().bind(tableView.widthProperty().divide(14));

        TableColumn<SimpleSolver.Result, Parameter> price = new TableColumn("cena");
        price.prefWidthProperty().bind(tableView.widthProperty().divide(14));
        price.setCellValueFactory(p -> Bindings.createObjectBinding(p.getValue().getItem().getParameters().stream().filter(parameter -> parameter.getId().equals("price")).findFirst().get()));
        Callback<TableColumn<SimpleSolver.Result, Parameter>, TableCell<SimpleSolver.Result, Parameter>> cellValueChange = (column -> {
            return new TableCell<SimpleSolver.Result, Parameter>() {
                @Override
                protected void updateItem(Parameter p, boolean empty) {
                    super.updateItem(p, empty);
                    if (p == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(p.getValue());
                        switch (p.getMatching()) {
                            case POORLY:
                                //setTextFill(Color.DARKRED);
                                setStyle("-fx-background-color: red");
                                break;
                            case AVERAGELY:
                                setStyle("-fx-background-color: yellow");
                                //setTextFill(Color.DARKORANGE);
                                break;
                            case STRONGLY:
                                setStyle("-fx-background-color: green");
                                //setTextFill(Color.GREEN);
                        }
                    }
                }
            };
        });
        price.setCellFactory(cellValueChange);

        tableView.getColumns().addAll(id, name, weight, price);

        HashMap<String, String> map = new HashMap<>();
        map.put("201717", "seria procesora");
        map.put("201725", "taktowanie bazowe procesora");
        map.put("4329", "liczba rdzeni procesora");
        map.put("201745", "typ pamięci RAM");
        map.put("201757", "wielkość pamięci RAM");
        map.put("201769", "typ dysku twardego");
        map.put("82", "pojemność dysku");
        map.put("201785", "rodzaj karty graficznej");
        map.put("201793", "chipset karty graficznej");
        map.put("201865", "system operacyjny");
        for (String s : map.keySet()) {
            TableColumn<SimpleSolver.Result, Parameter> p = new TableColumn(map.get(s));
            p.prefWidthProperty().bind(tableView.widthProperty().divide(14));
            p.setCellValueFactory(param -> Bindings.createObjectBinding(param.getValue().getItem().getParameters().stream().filter(parameter -> parameter.getId().equals(s)).findFirst().get()));
            p.setCellFactory(cellValueChange);
            tableView.getColumns().add(p);
        }
        tableView.setItems(data);

    }

    public AnchorPane getAnchorPane() {
        return anchorPane;
    }

    public void addResultsToList(List<SimpleSolver.Result> list) {
        data.addAll(list);
    }

    public void addResultToList(SimpleSolver.Result result) {
        data.add(result);
    }
}

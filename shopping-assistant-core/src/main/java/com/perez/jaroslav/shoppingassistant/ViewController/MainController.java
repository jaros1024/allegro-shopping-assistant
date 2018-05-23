package com.perez.jaroslav.shoppingassistant.ViewController;

import com.perez.jaroslav.allegrosearchapi.AllegroApi;
import com.perez.jaroslav.allegrosearchapi.ItemLoader;
import com.perez.jaroslav.shoppingassistant.sat4j.ResultsReceiver;
import com.perez.jaroslav.shoppingassistant.weight.Alternative;
import com.perez.jaroslav.shoppingassistant.weight.AlternativeComparePair;
import com.perez.jaroslav.shoppingassistant.weight.InputAlternative;
import com.perez.jaroslav.shoppingassistant.weight.WeightManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.util.List;


public class MainController {

    @FXML
    private Button nextButton;

    @FXML
    BorderPane borderMain;
    @FXML
    BorderPane mainView;
    @FXML
    Label titleLabel;

    private enum Phase {TYPE, INPUT_DETAILS, COMPAREING, WEIGHTSRESULTS}

    private Phase phase = Phase.TYPE;
    private int actualComparePairList = -1;
    private int actualInput = -1;
    private WeightManager weightManager;
    private ChoicePaneController actualChoicePane;
    private InputPaneController controller;
    private List<AlternativeComparePair> mainPairs;
    private List<AlternativeComparePair> optionalPairs;
    private List<InputAlternative> inputs;
    private InputPaneController inputPaneController;
    private Alternative altType = new Alternative();
    private ItemLoader itemLoader;

    public MainController() {

    }

    @FXML
    void initialize() {
        weightManager = WeightManager.getInstance();
        titleLabel.setText("Wybierz interesujacy cie typ komputera");
        mainView.setCenter(new RadioButtonsController(altType, "Komputer stacjonarny", "Laptop").getVbox());
    }

    @FXML
    public void goToNextSection() {
        if (phase == Phase.TYPE) {
            int type = (altType.getName().equals("Laptop")) ? AllegroApi.TYPE_LAPTOP : AllegroApi.TYPE_PC;
            weightManager.getAllegroApi().setType(type);
            weightManager.initWeightManager();
            mainPairs = weightManager.getMain().getComparePairs();
            optionalPairs = weightManager.getMain().getOptionalComparePairs();
            inputs = weightManager.getMain().getAllInputAlternative();
            phase = Phase.INPUT_DETAILS;
        }
        if (phase == Phase.INPUT_DETAILS) {
            inputControl();
        }
        if (phase == Phase.COMPAREING) {
            comparingControl();
        } else if (phase == Phase.WEIGHTSRESULTS) {
            weightResultsControl();
        }

    }

    private void inputControl() {
        if (controller != null && !controller.correctData()) {
            showAlert(Alert.AlertType.ERROR, "Zle dane", "Prosze wprowadzic poprawne dane", "Dane odnosnie zakresu musza byc liczbami");
            return;
        }
        actualInput++;
        if (actualInput >= inputs.size()) {
            phase = Phase.COMPAREING;
            return;
        }
        controller = new InputPaneController(inputs.get(actualInput));
        mainView.setCenter(controller.getVbox());
        titleLabel.setText("Podaj satysfakcjonujacy cie zakres: ");
    }

    private void comparingControl() {
        //setZerosToOptionalWhenInactive();
        //setOnesToOptionalWhenActive();
        if (actualChoicePane == null) {
            actualChoicePane = new ChoicePaneController(mainPairs, optionalPairs);
            mainView.setCenter(actualChoicePane.getStackPane());
            titleLabel.setText("Porownywanie kategorii");
            return;
        }
        setOnesWhenOptionalComparisonIsZero();
        setOnesWhenMainComaparisonIsZero();
        if (isNextComparison()) {
            setNextComparison();
            actualChoicePane = new ChoicePaneController(mainPairs, optionalPairs);
            mainView.setCenter(actualChoicePane.getStackPane());
            //weightManager.getMain().calcWeights2();
            titleLabel.setText(weightManager.getMain().getSubAlternatives().get(actualComparePairList).getName());
        } else {
            weightManager.getMain().calcWeights2();
            AnchorPane anchorPane = new WeightResultsController(weightManager.getMain()).getAnchorPane();
            mainView.setCenter(anchorPane);
            BorderPane.setAlignment(anchorPane, Pos.CENTER);
            titleLabel.setText("Wyniki");
            phase = Phase.WEIGHTSRESULTS;
        }
    }

    private void weightResultsControl() {
        ResultController resultController = new ResultController();
        mainView.setCenter(resultController.getAnchorPane());
        /*HashMap<String, Alternative> alternatives = new HashMap<>();
        weightManager.getMain().getResult().forEach(p -> alternatives.put(p.getId(), p));
        ResultsReceiver resultsReceiver = new ResultsReceiver(resultController, alternatives, weightManager.getAllegroApi().getItemLoader());
        new Thread(resultsReceiver).start();*/

        ResultsReceiver resultsReceiver = new ResultsReceiver(resultController, weightManager.getMain().getBestAlternatives(), weightManager.getAllegroApi().getItemLoader());
        new Thread(resultsReceiver).start();

      /*  itemLoader = weightManager.getAllegroApi().getItemLoader();
        Thread thread = new Thread(itemLoader);
        thread.start();
*/
        /*while (itemLoader.hasMorePackets()){
            try {
                ItemPacket itemPacket=itemLoader.getNextPacket();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
       /* ItemPacket itemPacket;

        while (itemLoader.hasMorePackets()) {
            try {
                itemPacket = itemLoader.getNextPacket();
                SimpleSolver solver = new SimpleSolver();
                solver.setItems(itemPacket.getItems());
                solver.setAlternatives(alternatives);
                solver.getResults().stream().sorted().forEachOrdered(result -> System.out.println(result.getItem().getName() + " " + result.getValue()));
           /* weightManager.addAlternativesToWeightMaxSat();
            weightManager.addToWeightMaxSatItems(itemPacket);
            weightManager.getSolve();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }


    private boolean isNextComparison() {
        return weightManager.getMain().getSubAlternatives().size() - 1 > actualComparePairList;
    }

    private boolean setNextComparison() {
        actualComparePairList++;
        mainPairs = weightManager.getComparePairs(actualComparePairList);
        optionalPairs = weightManager.getOptionalComparePairs(actualComparePairList);
        return optionalPairs != null && mainPairs != null;
    }

    private void setOnesWhenOptionalComparisonIsZero() {
        if (actualChoicePane != null && optionalPairs != null) {
            for (AlternativeComparePair a : optionalPairs) {
                if (a.getMoreImportant() == 0.00)
                    a.setMoreImportant(1.0);
            }
        }
    }

    private void setOnesWhenMainComaparisonIsZero() {
        if (actualChoicePane != null && mainPairs != null) {
            for (AlternativeComparePair a : mainPairs) {
                if (a.getMoreImportant() == 0.00)
                    a.setMoreImportant(1.0);
            }
        }
    }

    private void setZerosToOptionalWhenInactive() {
        if (actualChoicePane != null && !actualChoicePane.getIsActiveSelected()) {
            if (optionalPairs != null) {
                for (AlternativeComparePair a : optionalPairs) {
                    a.setMoreImportant(0.0);
                }
            }
        }
    }

    private void setOnesToOptionalWhenActive() {
        if (actualChoicePane != null && actualChoicePane.getIsActiveSelected()) {
            if (optionalPairs != null) {
                for (AlternativeComparePair a : optionalPairs) {
                    if (a.getMoreImportant() == 0)
                        a.setMoreImportant(1.0);
                }
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

}

package com.perez.jaroslav.shoppingassistant.ViewController;

import com.perez.jaroslav.allegrosearchapi.AllegroApi;
import com.perez.jaroslav.shoppingassistant.sat4j.ResultsReceiver;
import com.perez.jaroslav.shoppingassistant.weight.Alternative;
import com.perez.jaroslav.shoppingassistant.weight.AlternativeComparePair;
import com.perez.jaroslav.shoppingassistant.weight.InputAlternative;
import com.perez.jaroslav.shoppingassistant.weight.WeightManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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

    private enum Phase {TYPE, INPUT_DETAILS, COMPAREING, WEIGHTSRESULTS, END}

    private Phase phase = Phase.TYPE;
    private int actualComparePairList = -1;
    private int actualInput = -1;
    private WeightManager weightManager;
    private ChoicePaneController actualChoicePane;
    private InputPaneController controller;
    private List<AlternativeComparePair> mainPairs;
    private List<AlternativeComparePair> optionalPairs;
    private List<InputAlternative> inputs;
    private Alternative altType = new Alternative();
    private ResultsReceiver receiverThread = null;
    private Parent parent;
    private Task task;

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
            titleLabel.setText(weightManager.getMain().getSubAlternatives().get(actualComparePairList).getName());
        } else {
            nextButton.setDisable(true);
            titleLabel.setText("Wyniki");
            ProgressTracker progressTracker = new ProgressTracker();
            task = progressTracker.createTask();
            mainView.setCenter(progressTracker.createLayout(task));
            new Thread(task).start();
            new Thread(() -> {
                weightManager.getMain().calcWeights2();
                AnchorPane anchorPane = new WeightResultsController(weightManager.getMain()).getAnchorPane();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        mainView.setCenter(anchorPane);
                        BorderPane.setAlignment(anchorPane, Pos.CENTER);
                        task.cancel();
                        phase = Phase.WEIGHTSRESULTS;
                        nextButton.setDisable(false);
                    }
                });
            }).start();
        }
    }

    private void weightResultsControl() {
        nextButton.setDisable(true);
        ProgressTracker progressTracker = new ProgressTracker();
        task = progressTracker.createTask();
        mainView.setCenter(progressTracker.createLayout(task));
        new Thread(task).start();
        ResultController resultController = new ResultController(task,mainView);
        receiverThread = new ResultsReceiver(resultController, weightManager.getMain(), weightManager.getAllegroApi().getItemLoader());
        new Thread(receiverThread).start();
        phase = Phase.END;
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

    public void stopThread() {
        if (receiverThread != null) {
            receiverThread.setStop(true);
        }
        if(task!=null && task.isRunning()){
            task.cancel();
        }
    }

    public Parent getParent() {
        return parent;
    }
}

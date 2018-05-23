package com.perez.jaroslav.shoppingassistant.sat4j;

import com.perez.jaroslav.allegrosearchapi.ItemLoader;
import com.perez.jaroslav.allegrosearchapi.items.Item;
import com.perez.jaroslav.allegrosearchapi.items.ItemPacket;
import com.perez.jaroslav.shoppingassistant.ViewController.ResultController;
import com.perez.jaroslav.shoppingassistant.simplesolver.SimpleSolver;
import com.perez.jaroslav.shoppingassistant.weight.Alternative;
import com.perez.jaroslav.shoppingassistant.weight.SelectAlternative;

import java.util.List;

public class ResultsReceiver implements Runnable {

    private ResultController resultController;
    private List<Alternative> alternatives;
    private ItemLoader loader;

    public ResultsReceiver(ResultController resultController, List<Alternative> alternatives, ItemLoader loader) {
        this.resultController = resultController;
        this.alternatives = alternatives;
        this.loader = loader;
    }

    @Override
    public void run() {
        Thread thread = new Thread(loader);
        thread.start();
        ItemPacket itemPacket;
        while (loader.hasMorePackets()) {
            try {
                itemPacket = loader.getNextPacket();
                for (Item item : itemPacket.getItems()) {
                    /*SimpleWeightMaxSat simpleWeightMaxSat= new SimpleWeightMaxSat();
                    simpleWeightMaxSat.setBestAlternatives(alternatives);
                    resultController.addResultToList(simpleWeightMaxSat.solve(item));*/
                    WeightMaxSat2 weightMaxSat = new WeightMaxSat2();
                    weightMaxSat.setMain((SelectAlternative) alternatives.get(0));
                    SimpleSolver.Result r = weightMaxSat.solve(item);
                    if (r.getValue() > 0.01)
                        resultController.addResultToList(r);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

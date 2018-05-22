package com.perez.jaroslav.shoppingassistant.simplesolver;

import com.perez.jaroslav.allegrosearchapi.ItemLoader;
import com.perez.jaroslav.allegrosearchapi.items.ItemPacket;
import com.perez.jaroslav.shoppingassistant.ViewController.ResultController;
import com.perez.jaroslav.shoppingassistant.weight.Alternative;

import java.util.HashMap;

public class ResultsReceiver implements Runnable {

    private ResultController resultController;
    private HashMap<String, Alternative> alternatives;
    private ItemLoader loader;

    public ResultsReceiver(ResultController resultController, HashMap<String, Alternative> alternatives, ItemLoader loader) {
        this.resultController = resultController;
        this.alternatives = alternatives;
        this.loader = loader;
    }

    @Override
    public void run() {
        Thread thread = new Thread(loader);
        thread.start();
        ItemPacket itemPacket;
        SimpleSolver solver = new SimpleSolver();
        solver.setAlternatives(alternatives);
        while (loader.hasMorePackets()) {
            try {
                itemPacket = loader.getNextPacket();
                solver.setItems(itemPacket.getItems());
                resultController.addResultsToList(solver.getResults());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

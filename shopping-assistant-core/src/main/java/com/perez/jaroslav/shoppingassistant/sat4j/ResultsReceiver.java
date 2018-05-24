package com.perez.jaroslav.shoppingassistant.sat4j;

import com.perez.jaroslav.allegrosearchapi.ItemLoader;
import com.perez.jaroslav.allegrosearchapi.items.Item;
import com.perez.jaroslav.allegrosearchapi.items.ItemPacket;
import com.perez.jaroslav.shoppingassistant.ViewController.ResultController;
import com.perez.jaroslav.shoppingassistant.weight.Alternative;
import com.perez.jaroslav.shoppingassistant.weight.SelectAlternative;

public class ResultsReceiver implements Runnable {

    private ResultController resultController;
    private Alternative alternative;
    private ItemLoader loader;
    private boolean stop = false;

    public ResultsReceiver(ResultController resultController, Alternative alternative, ItemLoader loader) {
        this.resultController = resultController;
        this.alternative = alternative;
        this.loader = loader;
    }

    @Override
    public void run() {
        Thread thread = new Thread(loader);
        thread.start();
        ItemPacket itemPacket;
        while (loader.hasMorePackets() && !stop) {
            try {
                itemPacket = loader.getNextPacket();
                for (Item item : itemPacket.getItems()) {
                    WeightMaxSat weightMaxSat = new WeightMaxSat();
                    weightMaxSat.setMain((SelectAlternative) alternative);
                    WeightMaxSat.Result r = weightMaxSat.solve(item);
                    if (r.getValue() > 0.01)
                        resultController.addResultToList(r);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}

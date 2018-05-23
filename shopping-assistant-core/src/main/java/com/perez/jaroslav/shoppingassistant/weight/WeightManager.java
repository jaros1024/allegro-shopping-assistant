package com.perez.jaroslav.shoppingassistant.weight;

import com.perez.jaroslav.allegrosearchapi.AllegroApi;
import com.perez.jaroslav.allegrosearchapi.filters.Filter;
import com.perez.jaroslav.allegrosearchapi.filters.FilterOption;
import com.perez.jaroslav.allegrosearchapi.filters.InputFilter;
import com.perez.jaroslav.allegrosearchapi.filters.SelectFilter;
import com.perez.jaroslav.allegrosearchapi.items.Item;
import com.perez.jaroslav.allegrosearchapi.items.ItemPacket;
import com.perez.jaroslav.shoppingassistant.sat4j.WeightMaxSat;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class WeightManager {

    private static WeightManager weightManager = null;
    private static final ReentrantLock LOCK = new ReentrantLock();
    private AllegroApi allegroApi;
    private SelectAlternative main;
    private WeightMaxSat weightMaxSat;
    private List<Item> items = new ArrayList<>();

    private WeightManager() {

    }

    public static WeightManager getInstance() {
        synchronized (LOCK) {
            if (weightManager == null) {
                weightManager = new WeightManager();
            }
        }
        return weightManager;
    }

    public void initAllegroApi(String login, String password, String token) {
        allegroApi = new AllegroApi(login, password, token);
        allegroApi.setType(AllegroApi.TYPE_LAPTOP);
    }

    public SelectAlternative getMain() {
        return main;
    }

    public void setMain(SelectAlternative main) {
        this.main = main;
    }

    public List<AlternativeComparePair> getComparePairs(int i) {
        if (i < main.getSubAlternatives().size()) {
            return main.getSubAlternatives().get(i).getComparePairs();
        }
        return null;
    }

    public List<AlternativeComparePair> getOptionalComparePairs(int i) {
        if (i < main.getSubAlternatives().size()) {
            return main.getSubAlternatives().get(i).getOptionalComparePairs();
        }
        return null;
    }

    public AllegroApi getAllegroApi() {
        return allegroApi;
    }

    public void initWeightManager() {
        List<Filter> list = weightManager.getAllegroApi().getCategoryFilters();
        SelectAlternative mainAlt = new SelectAlternative("1", "main", 1);
        for (Filter f : list) {
            if (f instanceof SelectFilter) {
                SelectFilter selectFilter = (SelectFilter) f;
                SelectAlternative sub = new SelectAlternative(selectFilter.getId(), selectFilter.getName(), 1);
                for (FilterOption s : selectFilter.getOptions()) {
                    sub.addToSubAlternatives(new SelectAlternative(s.getId(), s.getName(), 1));
                }
                mainAlt.addToSubAlternatives(sub);
            } else if (f instanceof InputFilter) {
                InputFilter selectFilter = (InputFilter) f;
                mainAlt.addToSubAlternatives(new InputAlternative(selectFilter.getId(), selectFilter.getName(), selectFilter.getMinValue(), selectFilter.getMaxValue()));
            }
        }
        weightManager.setMain(mainAlt);

        weightMaxSat = new WeightMaxSat();
    }

    public void addAlternativesToWeightMaxSat() {
        for (SelectAlternative s : main.getSubAlternatives()) {
            try {
                weightMaxSat.InitExactlyAndSingleSoftFromAlternatives(s.getResult());
            } catch (ContradictionException e) {
                e.printStackTrace();
            }
        }
    }

    public void addToWeightMaxSatItems(ItemPacket packet) {
        for (Item item : packet.getItems()) {
            items.add(item);
            try {
                weightMaxSat.addSet(item);
            } catch (ContradictionException e) {
                e.printStackTrace();
            }
        }

    }

    public void getSolve() throws TimeoutException, ContradictionException {
        weightMaxSat.solve();
    }

}

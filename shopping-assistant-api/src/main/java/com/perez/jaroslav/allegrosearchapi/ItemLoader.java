package com.perez.jaroslav.allegrosearchapi;

import com.perez.jaroslav.allegrosearchapi.filters.Filter;
import com.perez.jaroslav.allegrosearchapi.items.ItemPacket;
import com.perez.jaroslav.allegrosearchapi.items.ItemPacketBuilder;
import com.perez.jaroslav.allegrosearchapi.soap.*;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemLoader implements Runnable {
    private static final int MAX_RESULT_SIZE = 500;

    private AuthService authService;
    private int category;
    private ServicePort port;
    private boolean stop = false;

    private Integer totalItems;
    private int totalPackets;
    private AtomicInteger completedPackets = new AtomicInteger(0);
    private int offset = 0;
    private HashMap<String, Filter> filterMap;

    private BlockingQueue<ItemPacket> resultQueue = new LinkedBlockingQueue<>(100);

    ItemLoader(AuthService authService, HashMap<String, Filter> filterMap, int category, ServicePort port) {
        this.authService = authService;
        this.category = category;
        this.port = port;
        this.filterMap = filterMap;
    }

    @Override
    public void run() {
        do {
            ItemPacket packet = createItemPacket(getItems(offset));
            resultQueue.add(packet);
            completedPackets.incrementAndGet();
        } while (hasMorePackets() && !stop);
    }

    public boolean hasMorePackets() {
        if (totalItems == null) {
            return true;
        }
        return (completedPackets.get() < totalPackets);
    }

    public ItemPacket getNextPacket() throws InterruptedException {
        return resultQueue.take();
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    private List<ItemsListType> getItems(int start) {
        DoGetItemsListRequest request = new DoGetItemsListRequest();
        request.setWebapiKey(authService.getUserToken());
        request.setCountryId(1);
        request.setResultSize(MAX_RESULT_SIZE);
        request.setResultScope(3);
        request.setResultOffset(start);

        ArrayOfFilteroptionstype filter = new ArrayOfFilteroptionstype();
        addCategoryFilter(filter);
        addStateFilter(filter);
        request.setFilterOptions(filter);

        DoGetItemsListResponse response = port.doGetItemsList(request);
        if (totalItems == null) {
            setPacketsCountData(response.getItemsCount());
        }
        offset += MAX_RESULT_SIZE;
        return response.getItemsList().getItem();
    }

    private void addCategoryFilter(ArrayOfFilteroptionstype filters) {
        FilterOptionsType fotcat = new FilterOptionsType();
        fotcat.setFilterId("category");
        ArrayOfString categories = new ArrayOfString();
        categories.getItem().add(Integer.toString(category));
        fotcat.setFilterValueId(categories);
        filters.getItem().add(fotcat);
    }

    private void addStateFilter(ArrayOfFilteroptionstype filters) {
        FilterOptionsType fotcat = new FilterOptionsType();
        fotcat.setFilterId("condition");
        ArrayOfString conditionArray = new ArrayOfString();
        conditionArray.getItem().add("new");
        fotcat.setFilterValueId(conditionArray);
        filters.getItem().add(fotcat);
    }

    private ItemPacket createItemPacket(List<ItemsListType> items) {
        return new ItemPacketBuilder(authService, port, filterMap).createItemPacket(items, completedPackets.get() + 1);
    }

    private void setPacketsCountData(int totalItems) {
        this.totalItems = totalItems;
        totalPackets = (int) Math.ceil(((double) totalItems) / ((double) MAX_RESULT_SIZE));
    }
}

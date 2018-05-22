package com.perez.jaroslav.allegrosearchapi.items;

import com.perez.jaroslav.allegrosearchapi.AuthService;
import com.perez.jaroslav.allegrosearchapi.filters.Filter;
import com.perez.jaroslav.allegrosearchapi.soap.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ItemPacketBuilder {
    private AuthService authService;
    private ServicePort port;
    private HashMap<String, Filter> filterMap;

    private static final int MAX_REQUEST_ITEMS = 25;
    private static final int WORKER_THREADS_COUNT = 4;

    public ItemPacketBuilder(AuthService authService, ServicePort port, HashMap<String, Filter> filterMap) {
        this.authService = authService;
        this.port = port;
        this.filterMap = filterMap;

        if(!authService.isLogged()){
            authService.login();
        }
    }

    public ItemPacket createItemPacket(List<ItemsListType> items, int packetId){
        ItemPacket packet = new ItemPacket();
        ConcurrentLinkedQueue<Item> packetItems = new ConcurrentLinkedQueue<>();
        packet.setId(packetId);

        List<Long> itemIds = new ArrayList<>(MAX_REQUEST_ITEMS);
        ExecutorService executorService = Executors.newFixedThreadPool(WORKER_THREADS_COUNT);

        //create list of ids to get and submit the tasks to executor service
        for(ItemsListType itemsList : items){
            itemIds.add(itemsList.getItemId());
            if(itemIds.size() == MAX_REQUEST_ITEMS){
                Runnable worker = new MiniPacketWorker(packetItems, itemIds);
                executorService.execute(worker);
                itemIds = new ArrayList<>(MAX_REQUEST_ITEMS);
            }
        }

        //if there are any ids left, execute them too
        if(!itemIds.isEmpty()){
            Runnable worker = new MiniPacketWorker(packetItems, itemIds);
            executorService.execute(worker);
            itemIds.clear();
        }
        executorService.shutdown();
        while(!executorService.isTerminated()){}

        packet.setItems(new LinkedList<>(packetItems));
        return packet;
    }

    private class MiniPacketWorker implements Runnable {
        private ConcurrentLinkedQueue<Item> packetItems;
        private List<Long> itemIds;

        MiniPacketWorker(ConcurrentLinkedQueue<Item> packetItems, List<Long> itemIds){
            this.packetItems = packetItems;
            this.itemIds = itemIds;
        }

        @Override
        public void run() {
            getMiniPacket();
        }

        private void getMiniPacket(){
            DoGetItemsInfoRequest request = new DoGetItemsInfoRequest();
            request.setSessionHandle(authService.getSessionHandle());

            ArrayOfLong arrayOfLong = new ArrayOfLong();
            arrayOfLong.getItem().addAll(itemIds);
            request.setItemsIdArray(arrayOfLong);
            request.setGetAttribs(1);

            DoGetItemsInfoResponse response = port.doGetItemsInfo(request);

            for(ItemInfoStruct itemInfo : response.getArrayItemListInfo().getItem()){
                Item item = new Item();
                item.setId(itemInfo.getItemInfo().getItId());
                List<Parameter> parameters = new LinkedList<>();

                item.setName(itemInfo.getItemInfo().getItName());
                parameters.add(new Parameter("price", Float.toString(itemInfo.getItemInfo().getItBuyNowPrice())));

                for(AttribStruct attr : itemInfo.getItemAttribs().getItem()){
                    String id = attr.getAttribName();
                    if(filterMap.containsKey(id)){
                        String value = attr.getAttribValues().getItem().get(0);
                        parameters.add(new Parameter(filterMap.get(id).getId(), value));
                    }
                }
                item.setParameters(parameters);
                boolean isMissingParams = false;
                for(Filter f : filterMap.values()){
                    if(!item.hasParameterWithId(f.getId())){
                        isMissingParams = true;
                        break;
                    }
                }

                if(!isMissingParams){
                    packetItems.add(item);
                }
            }
        }
    }
}

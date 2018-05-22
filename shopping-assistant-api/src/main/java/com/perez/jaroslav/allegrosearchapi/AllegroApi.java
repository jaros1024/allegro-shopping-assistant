package com.perez.jaroslav.allegrosearchapi;

import com.perez.jaroslav.allegrosearchapi.filters.Filter;
import com.perez.jaroslav.allegrosearchapi.soap.*;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class AllegroApi {
    public static final int TYPE_PC = 486;
    public static final int TYPE_LAPTOP = 491;

    private String userToken;
    private ServicePort port;
    private int type;

    private FilterConverter converter = new FilterConverter();
    private AuthService authService;
    private ItemLoader itemLoader;
    private final ReentrantLock lock = new ReentrantLock();

    public AllegroApi(String login, String password, String userToken){
        this.userToken = userToken;
        ServiceService service = new ServiceService();
        port = service.getServicePort();
        authService = new AuthService(port, userToken, login, password);
    }

    public List<Filter> getCategoryFilters() {
        DoGetItemsListRequest request = new DoGetItemsListRequest();
        request.setWebapiKey(userToken);
        request.setCountryId(1);
        request.setResultSize(1);

        ArrayOfFilteroptionstype filter = new ArrayOfFilteroptionstype();
        FilterOptionsType fotcat = new FilterOptionsType();
        fotcat.setFilterId("category");
        ArrayOfString categories = new ArrayOfString();
        categories.getItem().add(Integer.toString(type));
        fotcat.setFilterValueId(categories);
        filter.getItem().add(fotcat);
        request.setFilterOptions(filter);

        DoGetItemsListResponse response = port.doGetItemsList(request);
        return converter.getPrettyFilters(response.getFiltersList().getItem());
    }

    public ItemLoader getItemLoader(){
        synchronized (lock){
            if(itemLoader == null) {
                if (!converter.isLoaded()) {
                    getCategoryFilters();
                }
                itemLoader = new ItemLoader(authService, converter.getPrettyFilterMap(), type, port);
            }
        }
        return itemLoader;
    }

    public List<CatInfoType> getAllCategories(){
        DoGetCatsDataRequest request = new DoGetCatsDataRequest();
        request.setCountryId(1);
        request.setWebapiKey(userToken);

        DoGetCatsDataResponse response = port.doGetCatsData(request);

        return response.getCatsList().getItem();
    }

    public void setType(int type) {
        this.type = type;
    }
}

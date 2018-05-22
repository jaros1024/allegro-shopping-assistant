package com.perez.jaroslav.allegrosearchapi;

import com.perez.jaroslav.allegrosearchapi.filters.Filter;
import com.perez.jaroslav.allegrosearchapi.filters.FilterOption;
import com.perez.jaroslav.allegrosearchapi.filters.InputFilter;
import com.perez.jaroslav.allegrosearchapi.filters.SelectFilter;
import com.perez.jaroslav.allegrosearchapi.soap.FilterValueType;
import com.perez.jaroslav.allegrosearchapi.soap.FiltersListType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class FilterConverter {
    private static final HashSet<String> FILTER_WHITELIST = new HashSet<>();
    private HashMap<String, Filter> filterMap;

    public FilterConverter(){
        initWhitelist();
    }

    public void makePrettyFilters(List<FiltersListType> inputFilters){
        filterMap = new HashMap<>();

        for(FiltersListType filter : inputFilters){
            if(!FILTER_WHITELIST.contains(filter.getFilterId())){
                continue;
            }

            Filter convertedFilter;
            if(filter.getFilterValues() != null){
                convertedFilter = convertToSelectFilter(filter);
            }
            else {
                convertedFilter = convertToInputFilter(filter);
            }
            filterMap.put(convertedFilter.getName(), convertedFilter);
        }
    }

    public List<Filter> getPrettyFilters(List<FiltersListType> inputFilters){
        if(filterMap == null){
            makePrettyFilters(inputFilters);
        }
        return new LinkedList<>(filterMap.values());
    }

    public HashMap<String, Filter> getPrettyFilterMap() {
        return filterMap;
    }

    public Filter convertToSelectFilter(FiltersListType inputFilter){
        List<FilterOption> filterOptions = new LinkedList<>();

        List<FilterValueType> values = inputFilter.getFilterValues().getItem();
        for(FilterValueType value : values){
            FilterOption option = new FilterOption(value.getFilterValueId(), value.getFilterValueName());
            filterOptions.add(option);
        }
        return new SelectFilter(inputFilter.getFilterId(), inputFilter.getFilterName(), filterOptions);
    }

    public Filter convertToInputFilter(FiltersListType inputFilter){
        return new InputFilter(inputFilter.getFilterId(), inputFilter.getFilterName());
    }

    public boolean isLoaded(){
        return (filterMap != null);
    }

    private void initWhitelist(){
        FILTER_WHITELIST.add("201717"); //seria procesora
        FILTER_WHITELIST.add("201725"); //taktowanie bazowe procesora
        FILTER_WHITELIST.add("4329"); //liczba rdzeni procesora
        FILTER_WHITELIST.add("201745"); //typ pamięci RAM
        FILTER_WHITELIST.add("201757"); //wielkość pamięci RAM
        FILTER_WHITELIST.add("201769"); //typ dysku twardego
        FILTER_WHITELIST.add("82"); //pojemność dysku
        FILTER_WHITELIST.add("201785"); //rodzaj karty graficznej
        FILTER_WHITELIST.add("201793"); //chipset karty graficznej
        FILTER_WHITELIST.add("201865"); //system operacyjny
        FILTER_WHITELIST.add("price"); //cena
    }
}

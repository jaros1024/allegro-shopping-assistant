package com.perez.jaroslav.shoppingassistant.simplesolver;

import com.perez.jaroslav.allegrosearchapi.items.Item;
import com.perez.jaroslav.allegrosearchapi.items.Parameter;
import com.perez.jaroslav.shoppingassistant.weight.Alternative;
import com.perez.jaroslav.shoppingassistant.weight.InputAlternative;
import com.perez.jaroslav.shoppingassistant.weight.SelectAlternative;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class SimpleSolver {
    private List<Item> items;
    private HashMap<String, Alternative> alternatives;

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void setAlternatives(HashMap<String, Alternative> alternatives) {
        this.alternatives = alternatives;
    }

    public List<Result> getResults(){
        List<Result> results = new LinkedList<>();
        for(Item item : items){
            double value = 0;
            for(Parameter param : item.getParameters()){
                Alternative alternative = alternatives.get(param.getId());
                if(alternative instanceof SelectAlternative){
                    Alternative subAlt= getSubAlternativeWithName((SelectAlternative)alternative,param.getValue());
                    if(subAlt!=null){
                        value += subAlt.getWeight();
                    }
                }
                else if(alternative instanceof InputAlternative){
                    if(isInputAlternativeSatisfied((InputAlternative)alternative, param)){
                        value += alternative.getWeight();
                    }
                }
            }
            Result result = new Result(item, value);
            results.add(result);
        }
        Collections.reverse(results);
        return results;
    }

    private Alternative getSubAlternativeWithName(SelectAlternative s,String name){
        return s.getResult().stream().filter(p->p.getName().equals(name)).findFirst().orElse(null);
    }
    private boolean isSelectAlternativeSatisfied(SelectAlternative alternative, Parameter param){
        return param.getValue().equals(alternative.getName());
    }

    private boolean isInputAlternativeSatisfied(InputAlternative alternative, Parameter param){
        NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
        try {
            Number aMin = format.parse(alternative.getMinValue());
            Number aMax = format.parse(alternative.getMaxValue());
            Number paramValue = format.parse(param.getValue());
            return (paramValue.doubleValue() >= aMin.doubleValue() && paramValue.doubleValue() <= aMax.doubleValue());
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    public static class Result implements Comparable {
        private Item item;
        private double value;

        public Result(Item item, double value) {
            this.item = item;
            this.value = value;
        }

        public Item getItem() {
            return item;
        }

        public double getValue() {
            return value;
        }

        @Override
        public int compareTo(Object o) {
            return Double.compare(this.value, ((Result)o).getValue());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Result result = (Result) o;
            return Double.compare(result.value, value) == 0 &&
                    Objects.equals(item, result.item);
        }

        @Override
        public int hashCode() {
            return Objects.hash(item, value);
        }
    }
}

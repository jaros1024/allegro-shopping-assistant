package com.perez.jaroslav.shoppingassistant.weight;

public class InputAlternative extends Alternative {

    String minValue;
    String maxValue;
    public InputAlternative(String id, String name, String minValue, String maxValue) {
        super(id,name,1);
        this.maxValue=maxValue;
        this.minValue=minValue;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public String toString() {
        return name+" "+minValue+" - "+ maxValue;
    }
}

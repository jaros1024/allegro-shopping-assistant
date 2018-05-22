package com.perez.jaroslav.allegrosearchapi.filters;

import java.util.Objects;

public class InputFilter implements Filter {
    private String id;
    private String name;
    private String minValue;
    private String maxValue;

    public InputFilter(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputFilter that = (InputFilter) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(minValue, that.minValue) &&
                Objects.equals(maxValue, that.maxValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, minValue, maxValue);
    }

    @Override
    public String toString() {
        return "InputFilter{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

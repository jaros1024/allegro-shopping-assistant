package com.perez.jaroslav.shoppingassistant.weight;

import com.perez.jaroslav.allegrosearchapi.items.Parameter;

import java.util.Objects;

public class Alternative implements Comparable {

    protected String id;
    protected String name;
    protected double weight = 1;
    protected Parameter.Matching weightStrenght = Parameter.Matching.POORLY;

    public Alternative() {
    }

    public Alternative(String id, String name, int weight) {
        this.name = name;
        this.weight = weight;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void increaseWeight() {
        weight += 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alternative that = (Alternative) o;
        return weight == that.weight &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, weight);
    }

    @Override
    public int compareTo(Object o) {
        if (weight < ((Alternative) o).getWeight()) {
            return 1;
        }
        if (weight == ((Alternative) o).getWeight()) {
            return 0;
        }
        if (weight > ((Alternative) o).getWeight()) {
            return -1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getWeightInt() {
        return (int) (weight * 1000);
    }

    public Parameter.Matching getWeightStrenght() {
        return weightStrenght;
    }

    public void setWeightStrenght(Parameter.Matching weightStrenght) {
        this.weightStrenght = weightStrenght;
    }
}

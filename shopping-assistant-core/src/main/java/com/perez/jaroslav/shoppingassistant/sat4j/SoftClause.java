package com.perez.jaroslav.shoppingassistant.sat4j;

import org.sat4j.core.VecInt;

public class SoftClause {
    private int weight;
    private VecInt vec;

    public SoftClause(int weight, VecInt vec) {
        this.weight = weight;
        this.vec = vec;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public VecInt getVec() {
        return vec;
    }

    public void setVec(VecInt id) {
        this.vec = id;
    }
}

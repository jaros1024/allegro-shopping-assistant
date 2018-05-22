package com.perez.jaroslav.shoppingassistant.weight;

public class AlternativeComparePair {

    private Alternative first;
    private Alternative second;
    private double moreImportant=0;
    private int i,j;

    public AlternativeComparePair(Alternative first, Alternative second,int i,int j) {
        this.first = first;
        this.second = second;
        this.i=i;
        this.j=j;
    }

    public Alternative getFirst() {
        return first;
    }

    public void setFirst(Alternative first) {
        this.first = first;
    }

    public Alternative getSecond() {
        return second;
    }

    public void setSecond(Alternative second) {
        this.second = second;
    }

    public double getMoreImportant() {
        return moreImportant;
    }

    public void setMoreImportant(double moreImportant) {
        this.moreImportant = moreImportant;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }
}

package com.perez.jaroslav.shoppingassistant.ahp.adt;

public class Round {


    public static void main (String[] args) {
        double x = 1.23456789;
        float y = 9.87654f;
        double z;
        float w;

        z = round(x,2);
        System.out.println(z);
        z = round(x,5);
        System.out.println(z);

        System.out.println();

        w = round(y,3);
        System.out.println(w);
        w = round(y,0);
        System.out.println(w);
    }


    public static double round(double val, int places) {
        long factor = (long)Math.pow(10,places);

        // Shift the decimal the correct number of places
        // to the right.
        val = val * factor;

        // Round to the nearest integer.
        long tmp = Math.round(val);

        // Shift the decimal the correct number of places
        // back to the left.
        return (double)tmp / factor;
    }


    public static float round(float val, int places) {
        return (float)round((double)val, places);
    }


}

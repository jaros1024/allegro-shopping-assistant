package com.perez.jaroslav.shoppingassistant.weight;

import com.perez.jaroslav.allegrosearchapi.items.Parameter;
import pl.edu.agh.talaga.PairwiseComparisons;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SelectAlternative extends Alternative {
    private List<SelectAlternative> subSelectAlternatives = new ArrayList<>();
    private List<InputAlternative> subInputAlternatives = new ArrayList<>();
    private List<AlternativeComparePair> comparePairs;
    private List<AlternativeComparePair> optionalComparePairs;
    private List<Alternative> result = new ArrayList<>();
    private static PairwiseComparisons pairwiseComparisons = new PairwiseComparisons("shopping-assistant-core/lib/pairwiseComparisons.R");

    public SelectAlternative(String id, String name, int i) {
        super(id, name, i);
    }

    public void createComparePairs() {
        subInputAlternatives.forEach(p -> result.add(p));
        subSelectAlternatives.forEach(p -> result.add(p));
        comparePairs = new ArrayList<>();
        optionalComparePairs = new ArrayList<>();
        int col = 1;
        for (int row = 0; row < result.size() - 1; row++) {
            comparePairs.add(new AlternativeComparePair(result.get(row), result.get(col), row, col));
            col += 1;
        }
        for (int row = 0; row < result.size() - 2; row++) {
            for (col = 2 + row; col < result.size(); col++) {
                optionalComparePairs.add(new AlternativeComparePair(result.get(row), result.get(col), row, col));
            }
        }
        Random r = new Random();
        if (optionalComparePairs.size() > 0) {
            int few = (int) Math.sqrt(result.size());
            for (int i = 0; i < few && optionalComparePairs.size() > 0; i++) {
                int n = r.nextInt(optionalComparePairs.size());
                comparePairs.add(optionalComparePairs.get(n));
                optionalComparePairs.remove(n);
            }
        }
    }

    public List<AlternativeComparePair> getComparePairs() {
        if (comparePairs == null)
            createComparePairs();
        return comparePairs;
    }

    public List<AlternativeComparePair> getOptionalComparePairs() {
        if (comparePairs == null)
            createComparePairs();
        return optionalComparePairs;
    }

    /*
    public PairWiseComparisonMatrix getMatrixWithComparePairs() {
        PairWiseComparisonMatrix P = new PairWiseComparisonMatrix(subSelectAlternatives.size(), false);
        if (comparePairs != null)
            for (AlternativeComparePair a : comparePairs)
                P.set(a.getI(), a.getJ(), a.getMoreImportant());
        if (optionalComparePairs != null)
            for (AlternativeComparePair a : optionalComparePairs)
                P.set(a.getI(), a.getJ(), a.getMoreImportant());
        return P;
    }
*/
    // PairwiseComparisons library
    public double[][] getMatrix() {
        if (comparePairs.size() + optionalComparePairs.size() == 0) return null;
        double[][] P = new double[result.size()][result.size()];
        for (int i = 0; i < P.length; i++) {
            P[i][i] = 1;
        }
        if (comparePairs != null)
            for (AlternativeComparePair a : comparePairs)
                setToMatrix(P, a.getI(), a.getJ(), a.getMoreImportant());
        if (optionalComparePairs != null)
            for (AlternativeComparePair a : optionalComparePairs)
                setToMatrix(P, a.getI(), a.getJ(), a.getMoreImportant());
        return P;
    }

    public void setToMatrix(double[][] matrix, int i, int j, double value) {
        matrix[i][j] = value;
        if (value != 0)
            matrix[j][i] = 1 / value;
        else
            matrix[j][i] = 0;
    }

    public void calcWeights2() {
        if (result.size() > 0) {
            double[][] matrix = getMatrix();
            // matrix = pairwiseComparisons.koczkodajImprovedMatrixStep(matrix);
            double[] rank = pairwiseComparisons.eigenValueRank(matrix);
            for (int i = 0; i < result.size(); i++) {
                result.get(i).setWeight(weight * rank[i]);
                if (result.get(i) instanceof SelectAlternative)
                    ((SelectAlternative) result.get(i)).calcWeights2();
            }
            setWeightStrenghtToChilds();
        }
    }


    public void printMatrix(double[][] matrix) {
        System.out.println();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                System.out.printf("%.3f  ", matrix[i][j]);
            }
            System.out.println();
        }
    }

    public void printRank(double[] matrix) {
        System.out.println();
        for (int i = 0; i < matrix.length; i++) {
            System.out.printf("%.3f\n", matrix[i]);
            System.out.println();
        }
    }

    /*
    public void calcSingleWeight() {
        PairWiseComparisonMatrix p = getMatrixWithComparePairs();
        p.setHarkerMethod();
        p.calcWeights();
        if (p.isWeightsAllNans()) {
            p = getMatrixWithComparePairs();
            p.calcWeights();
        }
        for (int i = 0; i < subSelectAlternatives.size(); i++)
            subSelectAlternatives.get(i).setWeight(weight * p.getWeight(i));
        p.print();
        p.printWeights();
        System.out.println("InconsistencyRatio " + p.getInconsistencyRatio());
    }

    public void calcWeights() {
        PairWiseComparisonMatrix p = getMatrixWithComparePairs();
        if (p.getSize() > 0) {
            p.setHarkerMethod();
            p.calcWeights();
            if (p.isConsistency()) {
                System.out.println(name + " inconsistency");
            }
            if (p.isWeightsAllNans()) {
                p = getMatrixWithComparePairs();
                p.calcWeights();
                if (p.isConsistency()) {
                    System.out.println(name + " inconsistency");
                }
            }
            for (int i = 0; i < subSelectAlternatives.size(); i++) {
                subSelectAlternatives.get(i).setWeight(weight * p.getWeight(i));
                if( subSelectAlternatives.get(i) instanceof SelectAlternative)
                    ((SelectAlternative)subSelectAlternatives.get(i)).calcWeights();
            }
            p.print();
            p.printWeights();
            System.out.println("InconsistencyRatio " + p.getInconsistencyRatio());
        }
    }
*/
    public List<SelectAlternative> getSubAlternatives() {
        return subSelectAlternatives;
    }

    public void addToSubAlternatives(SelectAlternative a) {
        subSelectAlternatives.add(a);
    }

    public void addToSubAlternatives(InputAlternative a) {
        subInputAlternatives.add(a);
    }

    public List<Alternative> getResult() {
        return result;
    }

    public List<InputAlternative> getAllInputAlternative() {
        List<InputAlternative> inputAlternatives = new ArrayList<>();
        inputAlternatives.addAll(subInputAlternatives);
        subSelectAlternatives.forEach(p -> inputAlternatives.addAll(p.getAllInputAlternative()));
        return inputAlternatives;
    }

    public List<Alternative> getBestAlternatives() {
        List<Alternative> alt = new ArrayList<>();
        subInputAlternatives.forEach(p -> alt.add(p));
        for (SelectAlternative s : subSelectAlternatives) {
            // Alternative best = null;
            List<Alternative> list = s.getResult();
            list.sort((o1, o2) -> o1.getWeightInt() - o2.getWeightInt());
            for (int i = 0; i < Math.sqrt(list.size()); i++) {
                alt.add(list.get(i));
            }
          /*  for (Alternative sub : s.getResult())
                if (best == null || sub.getWeight() > best.getWeight())
                    best = sub;
            alt.add(best);*/
        }
        return alt;
    }

    @Override
    public String toString() {
        return name;
    }

    public List<Alternative> getAlternatives() {
        List<Alternative> alt = new ArrayList<>();
        subInputAlternatives.forEach(p -> alt.add(p));
        subSelectAlternatives.forEach(p ->
                p.getResult().forEach(sub -> alt.add(sub)));
        return alt;
    }

    private double findMinWeight(List<Alternative> list) {
        double min = 1;
        for (Alternative alternative : list)
            if (min > alternative.getWeight())
                min = alternative.getWeight();
        return min;
    }

    private double findMaxWeight(List<Alternative> list) {
        double max = 0;
        for (Alternative alternative : list)
            if (max < alternative.getWeight())
                max = alternative.getWeight();
        return max;
    }

    private void setWeightStrenghtToChilds() {
        for (SelectAlternative selectAlternative : subSelectAlternatives) {
            double min = findMinWeight(selectAlternative.getResult());
            double max = findMaxWeight(selectAlternative.getResult());
            double range = (max - min) / 5.0;
            for (Alternative a : selectAlternative.getResult()) {
                if (a.getWeight() < min + 2*range)
                    a.setWeightStrenght(Parameter.Matching.POORLY);
                else if (a.getWeight() < min + 4 * range)
                    a.setWeightStrenght(Parameter.Matching.AVERAGELY);
                else
                    a.setWeightStrenght(Parameter.Matching.STRONGLY);
            }
        }
    }
}

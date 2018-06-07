package com.perez.jaroslav.shoppingassistant.sat4j;

import com.perez.jaroslav.allegrosearchapi.items.Item;
import com.perez.jaroslav.allegrosearchapi.items.Parameter;
import com.perez.jaroslav.shoppingassistant.weight.Alternative;
import com.perez.jaroslav.shoppingassistant.weight.InputAlternative;
import com.perez.jaroslav.shoppingassistant.weight.SelectAlternative;
import org.sat4j.core.VecInt;
import org.sat4j.maxsat.SolverFactory;
import org.sat4j.maxsat.WeightedMaxSatDecorator;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;
import org.sat4j.tools.OptToSatAdapter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.function.Predicate;

public class WeightMaxSat {

    private WeightedMaxSatDecorator maxSatDecorator;
    private SelectAlternative main;
    private HashMap<Integer, Alternative> reverseMap;

    public WeightMaxSat() {
        reverseMap = new HashMap<>();
    }

    public void setMain(SelectAlternative main) {
        this.main = main;
    }

    public void initSimpleClause() throws ContradictionException {
        int counter = 0;
        if (reverseMap.size() == 0) {
            for (InputAlternative inputAlternative : main.getAllInputAlternative()) {
                counter++;
                maxSatDecorator.addSoftClause(inputAlternative.getWeightInt(), new VecInt(new int[]{counter}));
                reverseMap.put(counter, inputAlternative);
            }

            for (SelectAlternative s : main.getSubAlternatives()) {
                VecInt vecInt = new VecInt();
                List<Alternative> list = s.getResult();
                //list.sort((o1, o2) -> o2.getWeightInt() - o1.getWeightInt());
                for (int i = 0; i < list.size(); i++) {
                    counter++;
                    maxSatDecorator.addSoftClause(list.get(i).getWeightInt(), new VecInt(new int[]{counter}));
                    reverseMap.put(counter, list.get(i));
                    vecInt.push(counter);
                }
                maxSatDecorator.addExactly(vecInt, 1);
            }
        }
    }

    public Result solve(Item item) {
        maxSatDecorator = new WeightedMaxSatDecorator(SolverFactory.newDefault());
        final int MAXVAR = calcVar();
        final int NBCLAUSES = MAXVAR + main.getSubAlternatives().size() + item.getParameters().size();
        ModelIterator solver = new ModelIterator(new OptToSatAdapter(new PseudoOptDecorator(maxSatDecorator)));
        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);
        double weight = 0;
        try {
            initSimpleClause();
            for (Parameter parameter : item.getParameters()) {
                int pId = searchForParameter(parameter);
                if (pId != 0) {
                    maxSatDecorator.addHardClause(new VecInt(new int[]{pId}));
                }
            }
            weight = gatherResult(item, solver, reverseMap);
        } catch (ContradictionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return new Result(item, weight);
    }

    public int calcVar() {
        int var = 0;
        var += main.getAllInputAlternative().size();
        for (SelectAlternative s : main.getSubAlternatives())
            var += s.getResult().size();

        return var;
    }

    private int searchForParameter(Parameter p) {
        for (Integer i : reverseMap.keySet()) {
            Alternative alt = reverseMap.get(i);
            if (alt instanceof InputAlternative) {
                if (p.getId().equals(alt.getId()))
                    if (isInputAlternativeSatisfied((InputAlternative) reverseMap.get(i), p)) {
                        return i;
                    } else
                        return -i;
            } else if (alt instanceof SelectAlternative) {
                if (p.getValue().equals(alt.getName()))
                    return i;
            }
        }
        return 0;
    }

    private double gatherResult(Item item, ModelIterator solver, HashMap<Integer, Alternative> reverseMap) throws TimeoutException {
        double weight = 0;
        if (solver.isSatisfiable()) {
            System.out.println(solver.model().length);
            for (int i = 1; i <= solver.model().length; i++) {
                System.out.print(solver.model(i) + " ");
                if (solver.model(i)) {
                    weight += reverseMap.get(i).getWeight();
                    setIsMatchingToParameter(item, reverseMap.get(i));
                }
            }
            System.out.println();
        }
        return weight;
    }

    private Parameter getParameterForAlt(Item item, Alternative alt) {
        if (alt != null) {
            Predicate<Parameter> p = null;
            if (alt instanceof InputAlternative)
                p = parameter -> parameter.getId().equals(alt.getId());
            else
                p = parameter -> parameter.getValue().equals(alt.getName());
            Optional<Parameter> parameter = item.getParameters().stream().filter(p).findAny();
            return (parameter.isPresent()) ? parameter.get() : null;
        }
        return null;
    }

    private void setIsMatchingToParameter(Item item, Alternative alt) {
        Parameter p = getParameterForAlt(item, alt);
        if (p != null)
            p.setMatching(alt.getWeightStrenght());
    }

    private boolean isInputAlternativeSatisfied(InputAlternative alternative, Parameter param) {
        NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
        try {
            Number aMin = format.parse(alternative.getMinValue());
            Number aMax = format.parse(alternative.getMaxValue());
            Double paramValue = Double.valueOf(param.getValue());
            if (paramValue >= aMin.doubleValue() && paramValue <= aMax.doubleValue()) {
                alternative.setWeightStrenght(Parameter.Matching.STRONGLY);
                return true;
            } else {
                alternative.setWeightStrenght(Parameter.Matching.POORLY);
                return false;
            }
        } catch (ParseException e) {
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
            return Double.compare(this.value, ((Result) o).getValue());
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

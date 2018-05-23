package com.perez.jaroslav.shoppingassistant.sat4j;

import com.perez.jaroslav.allegrosearchapi.items.Item;
import com.perez.jaroslav.allegrosearchapi.items.Parameter;
import com.perez.jaroslav.shoppingassistant.simplesolver.SimpleSolver;
import com.perez.jaroslav.shoppingassistant.weight.Alternative;
import com.perez.jaroslav.shoppingassistant.weight.InputAlternative;
import org.sat4j.core.VecInt;
import org.sat4j.maxsat.SolverFactory;
import org.sat4j.maxsat.WeightedMaxSatDecorator;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.specs.ContradictionException;
import org.sat4j.tools.ModelIterator;
import org.sat4j.tools.OptToSatAdapter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;

public class SimpleWeightMaxSat {

    private WeightedMaxSatDecorator maxSatDecorator;
    private List<Alternative> best;

    public void setBestAlternatives(List<Alternative> alternatives) {
        best = alternatives;
    }

    public SimpleSolver.Result solve(Item item) {
        final int MAXVAR = best.size();
        final int NBCLAUSES = best.size() * 2;
        HashMap<String, Integer> map = new HashMap<>();
        HashMap<Integer, Alternative> reverseMap = new HashMap<>();
        Integer counter = 0;
        maxSatDecorator = new WeightedMaxSatDecorator(SolverFactory.newDefault());
        ModelIterator solver = new ModelIterator(new OptToSatAdapter(new PseudoOptDecorator(maxSatDecorator)));
        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);
        double weight = 0;
        try {
            initBestAlternativeToMaxSat(map, reverseMap, counter);
            for (Alternative b : best) {
                Parameter p = getParameterForAlt(item, b);
                if (p == null || ((b instanceof InputAlternative) && !isInputAlternativeSatisfied((InputAlternative) b, p))) {
                    maxSatDecorator.addHardClause(new VecInt(new int[]{-map.get(b.getId())}));
                } else
                    maxSatDecorator.addHardClause(new VecInt(new int[]{map.get(b.getId())}));
            }
            while (solver.isSatisfiable()) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new SimpleSolver.Result(item, weight);
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
            p.setMatching(true);
    }

    private void initBestAlternativeToMaxSat(HashMap<String, Integer> map, HashMap<Integer, Alternative> reverseMap, Integer counter) throws
            ContradictionException {
        if (maxSatDecorator != null) {
            for (Alternative b : best) {
                counter++;
                map.put(b.getId(), counter);
                reverseMap.put(counter, b);
                maxSatDecorator.addSoftClause(b.getWeightInt(), new VecInt(new int[]{counter}));
            }
        }
    }

    private boolean isInputAlternativeSatisfied(InputAlternative alternative, Parameter param) {
        NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
        try {
            Number aMin = format.parse(alternative.getMinValue());
            Number aMax = format.parse(alternative.getMaxValue());
            Double paramValue = Double.valueOf(param.getValue());
            return (paramValue >= aMin.doubleValue() && paramValue <= aMax.doubleValue());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}

package com.perez.jaroslav.shoppingassistant.sat4j;

import com.perez.jaroslav.allegrosearchapi.items.Item;
import com.perez.jaroslav.allegrosearchapi.items.Parameter;
import com.perez.jaroslav.shoppingassistant.weight.Alternative;
import org.sat4j.core.VecInt;
import org.sat4j.maxsat.SolverFactory;
import org.sat4j.maxsat.WeightedMaxSatDecorator;
import org.sat4j.pb.PseudoOptDecorator;
import org.sat4j.pb.tools.DependencyHelper;
import org.sat4j.pb.tools.WeightedObject;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.GateTranslator;
import org.sat4j.tools.ModelIterator;
import org.sat4j.tools.OptToSatAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightMaxSat {
    private WeightedMaxSatDecorator maxSatDecorator;
    private ModelIterator solver;
    private Map<String, Integer> map;
    private int counter;
    private List<SoftClause> softClauses=new ArrayList<>();
    private List<SoftClause> exactlyClauses=new ArrayList<>();
    private VecInt sets=new VecInt();
    public WeightMaxSat() {
        //maxSatDecorator = new WeightedMaxSatDecorator(SolverFactory.newDefault());
        //solver = new ModelIterator(new OptToSatAdapter(new PseudoOptDecorator(maxSatDecorator)));
        map = new HashMap<>();
        counter = 0;
        exactlyClauses.add(new SoftClause(1,sets));
    }

    public void InitExactlyAndSingleSoftFromAlternatives(List<Alternative> alternativeList) throws ContradictionException {
        VecInt alternatives = new VecInt();
        for (Alternative alt : alternativeList) {
            if (alt.getWeightInt() == 0)
                continue;
            counter++;
            map.put(alt.getId(), counter);
            System.out.println("MAP PUT : " + alt.getName() + " " + counter);
            alternatives.push(counter);
           // maxSatDecorator.addSoftClause(alt.getWeightInt(), new VecInt(new int[]{counter}));
            softClauses.add(new SoftClause(alt.getWeightInt(), new VecInt(new int[]{counter})));
            System.out.println("SOFT : waga: " + alt.getWeightInt() + " klauzula " + counter);
        }
        if (alternatives.size() > 0) {
            //maxSatDecorator.addExactly(alternatives, 1);
            exactlyClauses.add(new SoftClause(1,alternatives));
            System.out.println("EXACTLY ONE :" + alternatives.toString());
        }
    }


    public static void main(String[] args) throws TimeoutException {
        // 1 - procesor A
        // 2 - procesor B
        // 3 - grafika A
        // 4 - grafika B
        // 5 - zestaw  A
        // 6 - zestaw  B
        final int MAXVAR = 20;
        final int NBCLAUSES = 6;
        WeightedMaxSatDecorator maxSatDecorator = new WeightedMaxSatDecorator(SolverFactory.newDefault());
        ModelIterator solver = new ModelIterator(new OptToSatAdapter(new PseudoOptDecorator(maxSatDecorator)));
        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);
        GateTranslator translator = new GateTranslator(maxSatDecorator);
        try {
            maxSatDecorator.addSoftClause(1, new VecInt(new int[]{1}));
            maxSatDecorator.addSoftClause(3, new VecInt(new int[]{2}));
            maxSatDecorator.addSoftClause(3, new VecInt(new int[]{3}));
            maxSatDecorator.addSoftClause(2, new VecInt(new int[]{4}));

            maxSatDecorator.addSoftClause(5, new VecInt(new int[]{2, -6}));
            maxSatDecorator.addSoftClause(5, new VecInt(new int[]{4, -6}));
            maxSatDecorator.addSoftClause(5, new VecInt(new int[]{-2, -4, 6}));
            maxSatDecorator.addSoftClause(5, new VecInt(new int[]{1, -5}));
            maxSatDecorator.addSoftClause(5, new VecInt(new int[]{3, -5}));
            maxSatDecorator.addSoftClause(5, new VecInt(new int[]{-1, -3, 5}));

            maxSatDecorator.addExactly(new VecInt(new int[]{1, 2}), 1);
            maxSatDecorator.addExactly(new VecInt(new int[]{3, 4}), 1);
            maxSatDecorator.addExactly(new VecInt(new int[]{5, 6}), 1);
            //translator.and(5,new VecInt(new int[]{1,3}));
            //translator.and(6,new VecInt(new int[]{2,4}));

        } catch (ContradictionException e) {
            e.printStackTrace();
        }
        /*for (int i = 0; i<NBCLAUSES;i++){
            int[] clause = {1,2,3,4};
            try {
                solver.addClause(new VecInt(clause)); // adapt Array to IVecInt
            } catch (ContradictionException e) {
                e.printStackTrace();
            }
        }*/
        System.out.println("Solver nVars: " + solver.nVars());
        while (solver.isSatisfiable()) {
            System.out.println(solver.model().length);
            for (int i = 1; i <= solver.model().length; i++) {
                System.out.print(solver.model(i) + " ");
            }
            System.out.println();
        }
        maxSatDecorator.printStat(System.out, " ");

    }

    public void addSet(Item item) throws ContradictionException {
        VecInt result = new VecInt();
        counter++;
        sets.push(counter);
        map.put(String.valueOf(item.getId()), counter);
        for (Parameter p : item.getParameters()) {
            if (map.containsKey(p.getId())) {
                int i = map.get(p.getId());
                //maxSatDecorator.addSoftClause(10000, new VecInt(new int[]{i, -counter}));
                softClauses.add(new SoftClause(10000, new VecInt(new int[]{i, -counter})));
                result.push(-i);
            }
        }
        result.push(counter);
        //maxSatDecorator.addSoftClause(10000, result);
        softClauses.add(new SoftClause(1000,result));
    }

    public void solve() throws TimeoutException, ContradictionException {
        final int MAXVAR = map.size();
        final int NBCLAUSES = softClauses.size()+exactlyClauses.size();
        WeightedMaxSatDecorator maxSatDecorator = new WeightedMaxSatDecorator(SolverFactory.newDefault());
        ModelIterator solver = new ModelIterator(new OptToSatAdapter(new PseudoOptDecorator(maxSatDecorator)));
        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);

        for(SoftClause softClause:softClauses)
            maxSatDecorator.addSoftClause(softClause.getWeight(),softClause.getVec());

        for(SoftClause softClause:exactlyClauses)
            maxSatDecorator.addExactly(softClause.getVec(),softClause.getWeight());

        System.out.println("Solver nVars: " + solver.nVars());
        while (solver.isSatisfiable()) {
            System.out.println(solver.model().length);
            for (int i = 1; i <= solver.model().length; i++) {
                System.out.print(solver.model(i) + " ");
            }
            System.out.println();
        }
        maxSatDecorator.printStat(System.out, " ");
    }

}

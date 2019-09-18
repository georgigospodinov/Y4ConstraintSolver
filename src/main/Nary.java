package main;

import nary.ECSPReader;
import nary.ECSProblem;
import nary.algorithms.NaryForwardChecking;
import nary.algorithms.NaryMaintainingArcConsistency;
import nary.constraints.Assignment;

import java.util.LinkedHashSet;

/**
 * Provides a main method to run the NarySolving algorithm.
 *
 * @author 150009974
 * @version 1.0
 */
public class Nary extends ArgumentParser {

    public static void main(String[] args) {
        Nary nary = new Nary(args);
        LinkedHashSet<Assignment> solutions = nary.runSatisfactionAlg();
        solutions.forEach(System.out::println);
        System.out.println("Solution count: " + solutions.size());
        long executionTime = nary.getExecutionTime();
        System.out.println("Found in: " + executionTime + " milliseconds");
        System.out.println("Node count: " + nary.getSearchTreeNodes());
        System.out.println("Arc revisions: " + nary.getArcRevisions());
    }

    /**
     * Constructs an {@link ArgumentParser} with the given arguments.
     *
     * @param args the command line arguments
     */
    public Nary(String[] args) {
        super(args);
    }

    @Override
    protected SolvingAlgorithm getAlgorithm(String cspfilename, String algname) {
        ECSProblem problem = ECSPReader.parse(cspfilename);
        problem.prepare();
        if (algname.equalsIgnoreCase("FC")) {
            return new NaryForwardChecking(problem);
        } else if (algname.equalsIgnoreCase("MAC")) {
            return new NaryMaintainingArcConsistency(problem);
        } else if (algname.equalsIgnoreCase("MAC3")) {
            return new NaryMaintainingArcConsistency(problem);
        } else {
            System.out.println("Algorithm not recognized!");
            System.out.println("Use one of \"FC\" or \"MAC3\"");
            return null;
        }
    }

}

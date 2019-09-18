package main;

import binary.BinaryCSP;
import binary.algorithms.ForwardChecking;
import binary.algorithms.MaintainingArcConsistency;
import binary.algorithms.Solution;
import nary.ECSPReader;
import nary.ECSProblem;
import nary.constraints.Assignment;

import java.util.LinkedHashSet;

/**
 * Provides a main method to run the extension implementation.
 *
 * @author 150009974
 * @version 2.0
 */
public class DualRepresentation extends ArgumentParser {

    public static void main(String[] args) {
        DualRepresentation ext = new DualRepresentation(args);
        LinkedHashSet<Solution> solutions = ext.runSatisfactionAlg();
        for (Solution solution : solutions) {
            Assignment decoded = ext.originalProblem.decodeSolution(solution);
            System.out.println(decoded);
        }
        System.out.println("Solution count: " + solutions.size());
        long executionTime = ext.getExecutionTime();
        System.out.println("Found in: " + executionTime + " milliseconds");
        System.out.println("Node count: " + ext.getSearchTreeNodes());
        System.out.println("Arc revisions: " + ext.getArcRevisions());
    }

    /** The given high-level problem. */
    private ECSProblem originalProblem;

    /**
     * Constructs an {@link DualRepresentation} instance
     * with the given arguments.
     *
     * @param args the command line arguments
     */
    public DualRepresentation(String[] args) {
        super(args);
    }

    @Override
    protected SolvingAlgorithm getAlgorithm(String cspfilename, String algname) {
        originalProblem = ECSPReader.parse(cspfilename);
        BinaryCSP csp = originalProblem.asBinaryCSP();
        if (algname.equalsIgnoreCase("FC")) {
            return new ForwardChecking(csp);
        } else if (algname.equalsIgnoreCase("MAC")) {
            return new MaintainingArcConsistency(csp);
        } else if (algname.equalsIgnoreCase("MAC3")) {
            return new MaintainingArcConsistency(csp);
        } else {
            System.out.println("Algorithm not recognized!");
            System.out.println("Use one of \"FC\" or \"MAC3\"");
            return null;
        }
    }

}

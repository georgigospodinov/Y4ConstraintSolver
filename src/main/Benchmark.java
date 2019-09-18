package main;

import binary.algorithms.Solution;

import java.util.LinkedHashSet;

/**
 * Provides a main method to run and compare the solving algorithms.
 *
 * @author 150009974
 * @version 1.1
 */
public class Benchmark {

    /** The code for the {@link binary.algorithms.ForwardChecking} algorithm. */
    private static final String FC = "FC";

    /** The code for {@link binary.algorithms.MaintainingArcConsistency} algorithm. */
    private static final String MAC = "MAC";

    /**
     * Runs the {@link binary.algorithms.ForwardChecking}
     * and {@link binary.algorithms.MaintainingArcConsistency} algorithms
     * on all specified problems.
     * Only the final statistics are printed.
     * All other information (such as solutions and logging) is discarded.
     * The first argument (called N) is the number of iterations.
     * Each solver is run N times on each input file.
     * The average results are printed to standard output.
     *
     * The second argument must be the number of solutions
     * to find for each problem.
     *
     * @param args the array of problem filenames to solve
     */
    public static void main(String[] args) {
        int iterationsPerFile = Integer.parseInt(args[0]);
        String loggingLevel = "0";
        String stud = "i get replaced in the loop";
        String[] fcArgs = {FC, stud, args[1], loggingLevel};
        String[] macArgs = {MAC, stud, args[1], loggingLevel};
        System.out.print("problem name,");
        System.out.print("FC solutions,FC time,FC nodes,FC revisions,");
        System.out.println("MAC3 solutions,MAC3 time,MAC3 nodes,MAC3 revisions");

        for (int i = 2; i < args.length; i++) {
            String problemFilename = args[i];
            System.out.print(problemFilename);
            System.out.print(",");
            fcArgs[1] = problemFilename;
            executeAndPrintStats(fcArgs, iterationsPerFile);
            System.out.print(",");

            macArgs[1] = problemFilename;
            executeAndPrintStats(macArgs, iterationsPerFile);
            System.out.println();
        }
    }

    /**
     * Executes the {@link Basic} constraint solver with the given arguments
     * and outputs the performance statistics to standard output.
     *
     * @param algArgs the arguments to give to the solver
     * @param iterations the number of times to run the algorithm
     */
    private static void executeAndPrintStats(String[] algArgs, int iterations) {
        double averageSolutionCount = 0;
        double averageTime = 0;
        double averageNodes = 0;
        double averageRevisions = 0;
        for (int i = 0; i < iterations; i++) {
            Basic alg = new Basic(algArgs);
            LinkedHashSet<Solution> solutions = alg.runSatisfactionAlg();
            averageSolutionCount += solutions.size();
            averageTime += alg.getExecutionTime();
            averageNodes += alg.getSearchTreeNodes();
            averageRevisions += alg.getArcRevisions();
        }

        averageSolutionCount /= iterations;
        averageTime /= iterations;
        averageNodes /= iterations;
        averageRevisions /= iterations;

        System.out.print(averageSolutionCount + ",");
        System.out.print(averageTime + ",");
        System.out.print(averageNodes + ",");
        System.out.print(averageRevisions);
    }


}

package main;

import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Defines methods that a constraint solving algorithm should provide.
 *
 * @author 150009974
 * @version 2.1
 */
public abstract class SolvingAlgorithm {

    /** The amount of time (in milliseconds) the algorithm took to complete. */
    private long solutionTime;

    /** The number of search tree nodes that the algorithm reached. */
    private long numberOfNodes;

    /** The total number of arc revisions performed. */
    private long revisions;

    /** @return the amount of time (in milliseconds) that the algorithm took */
    public long getSolutionTime() {
        return solutionTime;
    }

    /** @return the number of search tree nodes that the algorithm reached */
    public long getNumberOfNodes() {
        return numberOfNodes;
    }

    /** @return the number of revisions made during the search */
    public long getRevisions() {
        return revisions;
    }

    /** Increments the number of revisions made by 1. */
    protected void incrementRevisionCounter() {
        revisions++;
    }

    /** Increments the number of nodes by 1. */
    protected void incrementNumberOfNodes() {
        numberOfNodes++;
    }

    /**
     * Solves a constraint satisfaction problem,
     * tracking the time taken, the number of search tree nodes created,
     * and the total number of arc revisions.
     * If it finds the specified amount of solutions, it terminates early.
     *
     * @param solutionCount the amount of solutions to find
     * @param startConsistent whether the constraint problem
     *                        should be made consistent before solving
     *
     * @return the set of found solutions
     */
    public LinkedHashSet<? extends Map> solve(int solutionCount, boolean startConsistent) {
        numberOfNodes = 0;
        revisions = 0;
        if (solutionCount <= 0) {
            solutionCount = Integer.MAX_VALUE;
        }
        long start = System.currentTimeMillis();

        if (startConsistent) {
            makeConsistent();
        }
        LinkedHashSet<? extends Map> solutions = innerSolve(solutionCount);

        long end = System.currentTimeMillis();
        solutionTime = end - start;
        return solutions;
    }

    /** Makes the problem consistent before the solving. */
    protected abstract void makeConsistent();

    /**
     * Solves a constraint satisfaction problem.
     * This method is used internally in {@link #solve(int, boolean)}.
     *
     * @param amount the amount of solutions to find
     *
     * @return the set of found solutions
     */
    protected abstract LinkedHashSet<? extends Map> innerSolve(int amount);

}

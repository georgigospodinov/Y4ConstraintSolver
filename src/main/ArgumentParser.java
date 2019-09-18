package main;

import binary.algorithms.Solution;

import java.util.LinkedHashSet;

/**
 * Provides a main method to run the system.
 *
 * @author 150009974
 * @version 3.1
 */
public abstract class ArgumentParser {

    /** Index of the solver algorithm in the command line arguments. */
    private static final int ALG_INDEX = 0;

    /** Index of the CSP in the command line arguments. */
    private static final int CSP_INDEX = 1;

    /** Index of the solution count in the command line arguments. */
    private static final int COUNT_INDEX = 2;

    /** Index of the logging level in the command line arguments. */
    private static final int LOG_INDEX = 3;

    /** Index of the start consistent flag in the command line arguments. */
    private static final int START_CONSISTENT_INDEX = 4;

    /** A reference to the command line arguments, to avoid method arguments. */
    private String[] args;

    /** The solving algorithm to use. */
    private SolvingAlgorithm alg;

    /**
     * Constructs an {@link ArgumentParser} with the given arguments.
     *
     * @param args the command line arguments
     */
    public ArgumentParser(String[] args) {
        this.args = args;
    }

    /**
     * Parses the command line arguments
     * and runs the {@link SolvingAlgorithm}.
     *
     * @return the found {@link Solution}s
     */
    public LinkedHashSet runSatisfactionAlg() {
        if (!areArgsValid()) {
            return null;
        }
        Logging.setLogConfig(parseLoggingConfiguration());
        alg = getAlgorithm(args[CSP_INDEX], args[ALG_INDEX]);
        if (alg == null) {
            return null;
        }
        int count = parseSolutionCount();
        boolean consistentStart = startConsistent();
        return alg.solve(count, consistentStart);
    }

    /** @return the solving algorithm's total execution time */
    public long getExecutionTime() {
        return alg.getSolutionTime();
    }

    /** @return the number of nodes created during the algorithm's execution */
    public long getSearchTreeNodes() {
        return alg.getNumberOfNodes();
    }

    /** @return the total number of revisions made during solving */
    public long getArcRevisions() {
        return alg.getRevisions();
    }

    /**
     * Checks if the arguments are valid.
     * If not, also prints a usage description.
     *
     * @return true iff the arguments are usable
     */
    private boolean areArgsValid() {
        if (COUNT_INDEX <= args.length
                && args.length <= START_CONSISTENT_INDEX + 1) {
            return true;
        }

        System.out.println("Usage: java main.<class> <alg> <csp> [<count>, <log>, <consistent flag>]");
        System.out.println("<class> must be one of \"Basic\" or \"DualRepresentation\" ");
        System.out.println("<alg> must be one of \"FC\" or \"MAC3\"");
        System.out.println("<csp> must be a .csp file");
        System.out.print("<count> is the number of solutions to find");
        System.out.println(", defaults to 1");
        System.out.println("<log> is the level of logging to the screen");
        return false;
    }

    /**
     * Instantiates and returns a {@link SolvingAlgorithm},
     * depending on the command line arguments.
     *
     * @param cspfilename the name of the csp file
     * @param algname     the name of the solving algorithm to use
     *
     * @return the {@link SolvingAlgorithm} to run
     */
    protected abstract SolvingAlgorithm getAlgorithm(String cspfilename, String algname);

    /**
     * Parses and returns the amount of solutions to look for.
     * If that number is not provided as a command line argument,
     * it defaults to 1.
     *
     * @return the number of solutions to find
     */
    private int parseSolutionCount() {
        try {
            return Integer.parseInt(args[COUNT_INDEX]);
        } catch (IndexOutOfBoundsException e) {
            return 1;
        }
    }

    /**
     * Parses and returns the logging configuration.
     * If that number is not provided as a command line argument,
     * it defaults to 0 (solution only).
     *
     * @return the logging configuration
     */
    private int parseLoggingConfiguration() {
        try {
            return Integer.parseInt(args[LOG_INDEX]);
        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    /**
     * Parses and returns the start consistent flag.
     * If that flag is on, then the {@link SolvingAlgorithm} is told to
     * make the problem consistent before solving.
     * This flag is on by default, the command line argument is normall used to
     * disable it.
     *
     * @return whether the problem should be made consistent before solving
     */
    private boolean startConsistent() {
        try {
            return args[START_CONSISTENT_INDEX].equalsIgnoreCase("true");
        } catch (IndexOutOfBoundsException e) {
            return true;
        }
    }

}

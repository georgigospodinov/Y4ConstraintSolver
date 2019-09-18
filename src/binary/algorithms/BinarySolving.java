package binary.algorithms;

import binary.BinaryCSP;
import binary.types.Variable;
import main.Logging;
import main.SolvingAlgorithm;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Represents a Constraint Solving algorithm for {@link BinaryCSP}s.
 *
 * @author 150009974
 * @version 2.3
 */
public abstract class BinarySolving extends SolvingAlgorithm {

    /** The {@link BinaryCSP} being solved. */
    private BinaryCSP problem;

    /** The set of assigned variables. */
    private HashSet<Variable> past = new HashSet<>();

    /** The set of unassigned variables. */
    private HashSet<Variable> future = new HashSet<>();

    /** The number of solution to be found. */
    private int solutionCount = 1;

    /** The set of {@link Solution}s. */
    private LinkedHashSet<Solution> solutions = new LinkedHashSet<>();

    /**
     * Creates a {@link BinarySolving} instance
     * to solve the given {@link BinaryCSP}.
     *
     * @param csp the {@link BinaryCSP} to solve
     */
    public BinarySolving(BinaryCSP csp) {
        problem = csp;
        problem.forEachVariable(future::add);
    }

    @Override
    public LinkedHashSet<Solution> innerSolve(int amount) {
        solutionCount = amount;
        solve();
        return solutions;
    }

    @Override
    protected void makeConsistent() {
        LinkedHashSet<Prune> prunes = new LinkedHashSet<>();
        for (Variable v : future) {
            revise(v, prunes);
        }
        if (Logging.logVarStates()) {
            System.out.println("After initial revision:");
            System.out.println("prunes: " + prunes);
            System.out.println("future: " + future);
        }
    }

    /**
     * Finds solutions to the {@link BinaryCSP}.
     * Stops when the specified amount of {@link Solution}s is found.
     *
     * @see BinarySolving#solutionCount
     */
    private void solve() {
        incrementNumberOfNodes();
        Variable var = getSmallestDomainVariable();
        int val = var.getNextVal();
        leftBranch(var, val);
        // No need to search for more solutions.
        if (solutions.size() >= solutionCount) {
            return;
        }
        rightBranch(var, val);
        if (Logging.logBranches()) {
            System.out.println("----------------------");
        }
    }

    /**
     * If a solution is found, it is stored.
     * Also, if needed, logs the current state of the search to standard output.
     *
     * @return true iff a solution was found and stored
     */
    private boolean checkState() {
        if (areAllVariablesAssigned()) {
            solutions.add(new Solution(past));
            return true;
        }

        if (Logging.logVarStates()) {
            System.out.println("past: " + past);
            System.out.println("future: " + future);
        }
        return false;
    }

    /** @return the variable with the smallest domain */
    private Variable getSmallestDomainVariable() {
        Variable best = null;
        int smallestDomain = Integer.MAX_VALUE;
        for (Variable var : future) {
            if (var.getDomainSize() < smallestDomain) {
                smallestDomain = var.getDomainSize();
                best = var;
                // Cannot get better than 1.
                if (smallestDomain == 1) {
                    break;
                }
            }
        }
        return best;
    }

    /**
     * Performs the left branch of the search by assigning the given value
     * to the given {@link Variable}.
     *
     * @param var the {@link Variable} to be assigned
     * @param val the value to be assigned to the variable
     */
    private void leftBranch(Variable var, int val) {
        assign(var, val);
        if (Logging.logBranches()) {
            System.out.println(var);
            System.out.println("Left Branch");
        }
        if (!checkState()) {
            propagateChange(var);
        }
        unassign(var);
    }

    /**
     * Performs the right branch of the search by removing the given value
     * from the given {@link Variable}'s domain.
     *
     * @param var the {@link Variable} from which to remove the value
     * @param val the value to remove
     */
    private void rightBranch(Variable var, int val) {
        if (Logging.logBranches()) {
            System.out.println("Right Branch");
        }
        var.removeFromDomain(val);
        if (var.isConsistent()) {
            propagateChange(var);
        }
        var.addToDomain(val);
    }

    /** @return true iff all variables have been assigned */
    private boolean areAllVariablesAssigned() {
        return future.isEmpty();
    }

    /**
     * Assigns the given value to the given variable and tracks that
     * in the {@link BinarySolving#past}
     * and {@link BinarySolving#future} variables.
     *
     * @param var the {@link Variable} to be assigned
     * @param val the value to be assigned to the variable
     */
    private void assign(Variable var, int val) {
        var.assign(val);
        past.add(var);
        future.remove(var);
    }

    /**
     * Unassigns the given {@link Variable} and moves it from
     * the set of {@link BinarySolving#past} {@link Variable}s to
     * the set of {@link BinarySolving#future} {@link Variable}s.
     *
     * @param var the {@link Variable} to be unassigned
     */
    private void unassign(Variable var) {
        var.unassign();
        past.remove(var);
        future.add(var);
    }

    /**
     * Propagates the change to the given {@link Variable}
     * by revising future {@link Variable}s.
     * If all {@link Variable}s are consistent,
     * invokes the main solve method.
     * The pruning is undone when that invocation returns.
     *
     * @param var the changed {@link Variable} that triggered the revise
     */
    private void propagateChange(Variable var) {
        LinkedHashSet<Prune> pruned = new LinkedHashSet<>();
        boolean consistent = revise(var, pruned);
        recurseIfConsistent(consistent);
        pruned.forEach(Prune::undo);
    }

    /**
     * Invokes the {@link BinarySolving#solve()} method if
     * {@link Variable}s are consistent. Otherwise, prints "Domain wipeout!".
     * This method should be called by implementing classes in their
     * {@link BinarySolving#propagateChange(Variable)} methods.
     *
     * @param consistent whether the {@link Variable}s are consistent
     */
    private void recurseIfConsistent(boolean consistent) {
        if (consistent) {
            solve();
        } else if (Logging.logWipeouts()) {
            System.out.println("Domain wipeout!");
        }
    }

    /**
     * Revises {@link Variable}s, starting with those connected to
     * the given one. Stores all {@link Prune}s in the given set.
     * Returns true if and only if the problem is consistent after pruning.
     *
     * @param var    the changed {@link Variable} that triggered the revise
     * @param prunes the set where all {@link Prune}s are to be stored
     *
     * @return true iff the problem is consistent after pruning
     */
    protected abstract boolean revise(Variable var, LinkedHashSet<Prune> prunes);

    /**
     * Gets the {@link HashSet} of future {@link Variable}s
     * at the current state of the search.
     *
     * @return the future {@link Variable}s
     */
    public HashSet<Variable> getFuture() {
        return future;
    }

    /** @return the {@link BinaryCSP} being solved */
    public BinaryCSP getProblem() {
        return problem;
    }

}

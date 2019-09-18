package nary.algorithms;

import main.SolvingAlgorithm;
import nary.ECSProblem;
import nary.constraints.Assignment;
import nary.types.BaseVariable;

import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Represents a Constraint Solving algorithm for {@link ECSProblem}s.
 *
 * @author 150009974
 * @version 2.1
 */
public abstract class NarySolving extends SolvingAlgorithm {

    /** The {@link ECSProblem} being solved. */
    private ECSProblem problem;

    /** The currently assigned variables. */
    private Assignment past = new Assignment();

    /** The unassigned variables. */
    private HashSet<String> future = new HashSet<>();

    /** The number of solution to be found. */
    private int solutionCount = 1;

    /** The set of solutions found so far. */
    private LinkedHashSet<Assignment> solutions = new LinkedHashSet<>();

    /**
     * Creates a {@link NarySolving} instance
     * to solve the given {@link ECSProblem}.
     *
     * @param problem the {@link ECSProblem} to solve
     */
    public NarySolving(ECSProblem problem) {
        this.problem = problem;
        problem.forEachVariable((name, var) -> future.add(name));
    }

    @Override
    public LinkedHashSet<Assignment> innerSolve(int amount) {
        solutionCount = amount;
        solve();
        return solutions;
    }

    @Override
    protected void makeConsistent() {
        LinkedHashSet<BaseVarPrune> prunes = new LinkedHashSet<>();
        for (String fName : future) {
            BaseVariable f = problem.getVariable(fName);
            revise(f, prunes);
        }
    }

    /**
     * Finds solutions to the {@link #problem}.
     * Stops when the specified amount of solutions is found.
     *
     * @see #solutionCount
     */
    private void solve() {
        incrementNumberOfNodes();
        BaseVariable var = getSmallestDomainVariable();
        int val = var.getNextValue();
        leftBranch(var, val);
        // No need to search for more solutions.
        if (solutions.size() >= solutionCount) {
            return;
        }
        rightBranch(var, val);
    }

    /**
     * Performs the left branch of the search by assigning the given value
     * to the given {@link BaseVariable}.
     *
     * @param var the {@link BaseVariable} to be assigned
     * @param val the value to be assigned to the variable
     */
    private void leftBranch(BaseVariable var, int val) {
        assign(var, val);
        if (!checkState()) {
            propagateChange(var);
        }
        unassign(var);
    }

    /**
     * Performs the right branch of the search by removing the given value
     * from the given {@link BaseVariable}'s domain.
     *
     * @param var the {@link BaseVariable} from which to remove the value
     * @param val the value to remove
     */
    private void rightBranch(BaseVariable var, int val) {
        var.removeFromDomain(val);
        if (var.isConsistent()) {
            propagateChange(var);
        }
        var.addToDomain(val);
    }

    /**
     * Checks if a the current assignment is complete.
     * In that case saves a copy of it as a solution and returns true.
     *
     * @return true iff the current assignment is a solution and gets stored
     */
    private boolean checkState() {
        if (areAllVariablesAssigned()) {
            solutions.add(past.deepCopy());
            return true;
        }
        return false;
    }

    /** @return the variable with the smallest domain */
    private BaseVariable getSmallestDomainVariable() {
        BaseVariable best = null;
        int smallestDomain = Integer.MAX_VALUE;
        for (String fName : future) {
            BaseVariable var = problem.getVariable(fName);
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

    /** @return true iff all variables have been assigned */
    private boolean areAllVariablesAssigned() {
        return future.isEmpty();
    }

    /**
     * Assigns the given value to the given {@link BaseVariable} and moves it
     * from the {@link #future} to the {@link #past} variables.
     *
     * @param var the {@link BaseVariable} to be assigned
     * @param val the value to be assigned to the variable
     */
    private void assign(BaseVariable var, int val) {
        var.assign(val);
        past.put(var.getName(), val);
        future.remove(var.getName());
    }

    /**
     * Unassigns the given {@link BaseVariable} and moves it from {@link #past}
     * to {@link #future} variables.
     *
     * @param var the {@link BaseVariable} to be unassigned
     */
    private void unassign(BaseVariable var) {
        var.unassign();
        past.remove(var.getName());
        future.add(var.getName());
    }

    /**
     * Propagates the change to the given {@link BaseVariable}
     * by revising future {@link BaseVariable}s.
     * If all {@link BaseVariable}s are consistent,
     * invokes the main solve method.
     * The pruning is undone when that invocation returns.
     *
     * @param var the changed {@link BaseVariable} that triggered the revise
     */
    private void propagateChange(BaseVariable var) {
        LinkedHashSet<BaseVarPrune> pruned = new LinkedHashSet<>();
        boolean consistent = revise(var, pruned);
        if (consistent) {
            solve();
        }
        pruned.forEach(BaseVarPrune::undo);
    }

    /**
     * Revises {@link BaseVariable}s, starting with those
     * connected to the given one.
     * Stores all {@link BaseVarPrune}s in the given set.
     * Returns true if and only if the problem is consistent after pruning.
     *
     * @param var    the changed {@link BaseVariable} that triggered the revise
     * @param prunes the set where all {@link BaseVarPrune}s are to be stored
     *
     * @return true iff the problem is consistent after pruning
     */
    protected abstract boolean revise(BaseVariable var, LinkedHashSet<BaseVarPrune> prunes);

    /** @return the {@link ECSProblem} being solved */
    public ECSProblem getProblem() {
        return problem;
    }

    /** @return the currently assigned variable (and their weapons) */
    public Assignment getPast() {
        return past;
    }

    /** @return the unassiged variables ()*/
    public HashSet<String> getFuture() {
        return future;
    }

}

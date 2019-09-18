package nary.constraints;


import binary.types.BinaryConstraint;
import binary.types.BinaryTuple;
import nary.ECSProblem;
import nary.types.BaseVariable;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/**
 * Represents a constraint between multiple variables..
 *
 * @author 150009974
 * @version 1.5
 */
public class NaryConstraint {

    /**
     * Parses the given {@link String} as a {@link NaryConstraint} declaration
     * and returns the created {@link NaryConstraint}.
     * Returns null if the given {@link String} is not a valid
     * {@link NaryConstraint} declaration.
     *
     * @param declaration the nary constraint declaration
     *
     * @return the created {@link NaryConstraint}
     * or null if the given declaration is invalid
     */
    public static NaryConstraint parse(String declaration) {
        Expression exp = new Expression(declaration);
        String[] args = exp.getMissingUserDefinedArguments();
        if (args.length < 2) {
            return null;
        }
        return new NaryConstraint(exp);
    }

    /** The high-level constraint that this represents. */
    private Expression constraint;

    /** The {@link ECSProblem} that this constraint is part of. */
    private ECSProblem problem;

    /** Stores the valid {@link Assignment}s of {@link BaseVariable}s. */
    private ArrayList<Assignment> domain = new ArrayList<>();

    /**
     * Creates a {@link NaryConstraint} for the given {@link Expression}.
     *
     * @param exp the {@link Expression} of this {@link NaryConstraint}
     */
    private NaryConstraint(Expression exp) {
        constraint = exp;
    }

    /** @param problem the {@link ECSProblem} of this {@link NaryConstraint} */
    public void setProblem(ECSProblem problem) {
        this.problem = problem;
    }

    /**
     * Recursively generates all possible assignments of
     * the {@link BaseVariable}s of this {@link NaryConstraint}.
     * Assignments that satisfy the {@link NaryConstraint#constraint}
     * are stored.
     */
    public void storeValidAssignments() {
        String[] argsArr = constraint.getMissingUserDefinedArguments();
        List<String> args = Arrays.asList(argsArr);
        LinkedHashSet<String> unassigned = new LinkedHashSet<>(args);
        /* Define arguments in the Expression, so that they can be set
         * in storeIfValid(Assignment) using Expression.setArgumentValue().
         * Whether the arguments are actually assigned is determined
         * by the unassigned set.
         */
        constraint.removeAllArguments();
        for (String arg : args) {
            constraint.defineArgument(arg, 0);
        }
        storeValidAssignments(new Assignment(), unassigned);
    }

    /**
     * Recursively generates all possible assignments of
     * the {@link BaseVariable}s of this {@link NaryConstraint}.
     * Assignments that satisfy the {@link NaryConstraint#constraint}
     * are stored.
     *
     * @param assigned   the {@link Assignment} made so far
     * @param unassigned the names of the {@link BaseVariable}s
     *                   that are yet to be assigned
     */
    private void storeValidAssignments(Assignment assigned, LinkedHashSet<String> unassigned) {
        if (unassigned.isEmpty()) {
            storeIfValid(assigned);
            return;
        }

        String arg = unassigned.iterator().next();
        unassigned.remove(arg);
        BaseVariable var = problem.getVariable(arg);
        if (var == null) {
            String message = "Variable \"" + arg + "\" not declared: "
                    + constraint.getExpressionString();
            throw new NullPointerException(message);
        }
        LinkedHashSet<Integer> values = var.getDomain();
        for (Integer val : values) {
            assigned.put(arg, val);
            storeValidAssignments(assigned, unassigned);
        }
        assigned.remove(arg);
        unassigned.add(arg);
    }

    /**
     * Checks if the given {@link Assignment} satisfies
     * the {@link NaryConstraint#constraint} and stores it if it does.
     *
     * @param assigned the {@link Assignment} to check
     */
    private void storeIfValid(Assignment assigned) {
        assigned.forEach((arg, val) -> constraint.setArgumentValue(arg, val));
        if (constraint.calculate() == 1.0) {
            domain.add(assigned.deepCopy());
        }
    }

    /**
     * Constructs and returns all {@link BinaryTuple}s that are permitted
     * by the {@link BinaryConstraint} that combines
     * this {@link NaryConstraint} and that {@link NaryConstraint}.
     *
     * @param that the other {@link NaryConstraint}
     *
     * @return the allowed {@link BinaryTuple}s
     */
    public LinkedHashSet<BinaryTuple> getAllowedTuples(NaryConstraint that) {
        LinkedHashSet<BinaryTuple> allowedTuples = new LinkedHashSet<>();
        for (int i = 0; i < this.domain.size(); i++) {
            Assignment thisAssignment = this.domain.get(i);
            for (int j = 0; j < that.domain.size(); j++) {
                Assignment thatAssignment = that.domain.get(j);
                if (thisAssignment.isCompatibleWith(thatAssignment)) {
                    BinaryTuple tuple = new BinaryTuple(i, j);
                    allowedTuples.add(tuple);
                }
            }
        }
        return allowedTuples;
    }

    /**
     * Retrieves the size of the domain of {@link Assignment}s
     * that this {@link NaryConstraint} constraint allows.
     *
     * @return the domain size
     */
    public int getDomainSize() {
        return domain.size();
    }

    /** @return the {@link Assignment} at the specified index */
    public Assignment getValue(int index) {
        return domain.get(index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Assignment d : domain) {
            sb.append("\n");
            sb.append(d);
        }
        sb.append("]");
        return "NaryConstraint{"
                + "constraint=\"" + constraint.getExpressionString() + "\","
                + "domain=" + sb.toString()
                + "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(constraint);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NaryConstraint)) {
            return false;
        }
        NaryConstraint that = (NaryConstraint) o;
        return constraint.equals(that.constraint) &&
                problem.equals(that.problem) &&
                domain.equals(that.domain);
    }

    /**
     * Returns all supported values of the specified variable,
     * given the current {@link Assignment}.
     *
     * @param current the current {@link BaseVariable} assignment
     * @param varName the name of the {@link BaseVariable} to prune
     *
     * @return the supported values of the specified variable,
     * or null if this {@link NaryConstraint} does use the specified variable
     */
    public LinkedHashSet<Integer> getSupported(Assignment current, String varName) {
        if (!containsBaseVar(varName)) {
            return null;
        }
        LinkedHashSet<Integer> supported = new LinkedHashSet<>();
        for (Assignment d : domain) {
            if (d.isCompatibleWith(current)) {
                supported.add(d.get(varName));
            }
        }
        return supported;
    }

    /**
     * Checks if this {@link NaryConstraint} contains
     * the specified {@link BaseVariable}.
     *
     * @param varName the name of the {@link BaseVariable}
     *
     * @return true iff the specified {@link BaseVariable}
     * appears in this {@link NaryConstraint}
     *
     * @see BaseVariable#getName()
     */
    public boolean containsBaseVar(String varName) {
        return constraint.getArgument(varName) != null;
    }

    /**
     * Creates and returns an array of the names of {@link BaseVariable}s
     * that appear in this {@link NaryConstraint}.
     *
     * @return the created array of {@link BaseVariable} names
     */
    public String[] getVarNames() {
        int size = constraint.getArgumentsNumber();
        String[] names = new String[size];
        for (int i = 0; i < size; i++) {
            names[i] = constraint.getArgument(i).getArgumentName();
        }
        return names;
    }

}

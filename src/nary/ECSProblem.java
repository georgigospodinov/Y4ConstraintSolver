package nary;


import binary.BinaryCSP;
import binary.algorithms.Solution;
import binary.types.BinaryConstraint;
import binary.types.BinaryConstraintStorage;
import binary.types.BinaryTuple;
import binary.types.Variable;
import nary.constraints.Assignment;
import nary.constraints.NaryConstraint;
import nary.constraints.UnaryConstraint;
import nary.types.BaseVariable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.function.BiConsumer;

/**
 * Represents a ECSProblem.
 * A constraint satisfaction problem with more abstract constraints.
 *
 * @author 150009974
 * @version 1.7
 */
public class ECSProblem {

    /** The {@link BaseVariable}s of this {@link ECSProblem}. */
    private LinkedHashMap<String, BaseVariable> variables;

    /** The {@link UnaryConstraint}s between the variables. */
    private LinkedHashSet<UnaryConstraint> unary;

    /** The {@link NaryConstraint}s between the variables. */
    private ArrayList<NaryConstraint> nary;

    /** @param vars a map of names to {@link BaseVariable}s */
    public void setVariables(LinkedHashMap<String, BaseVariable> vars) {
        variables = vars;
    }

    /** @param unaryConstraints the {@link UnaryConstraint}s */
    public void setUnary(LinkedHashSet<UnaryConstraint> unaryConstraints) {
        unary = unaryConstraints;
    }

    /** @param naryConstraints the {@link NaryConstraint}s */
    public void setNary(ArrayList<NaryConstraint> naryConstraints) {
        nary = naryConstraints;
    }

    /** @return the {@link BaseVariable} with the specified name */
    public BaseVariable getVariable(String name) {
        return variables.get(name);
    }

    /**
     * Creates and returns a {@link BinaryCSP} representation
     * of this {@link ECSProblem}.
     *
     * @return a {@link BinaryCSP} representation of this {@link ECSProblem}
     */
    public BinaryCSP asBinaryCSP() {
        applyUnary();
        int[][] bounds = createBounds();
        BinaryConstraintStorage cs = createBinaryConstraints();
        return new BinaryCSP(bounds, cs);
    }

    /** Applies all {@link UnaryConstraint}s. */
    private void applyUnary() {
        unary.forEach(u -> u.apply(this));
    }

    /**
     * Creates the bounds for the {@link Variable}s.
     *
     * @return the {@link Variable}s' bounds
     */
    private int[][] createBounds() {
        int[][] bounds = new int[nary.size()][2];
        for (int i = 0; i < nary.size(); i++) {
            NaryConstraint n = nary.get(i);
            n.storeValidAssignments();
            bounds[i][0] = 0;
            bounds[i][1] = n.getDomainSize() - 1;
        }
        return bounds;
    }

    /**
     * Creates and returns {@link BinaryConstraint}s for
     * a {@link BinaryCSP} representation of this {@link ECSProblem}.
     *
     * @return a map from a {@link BinaryConstraint}'s hash code to itself
     */
    private BinaryConstraintStorage createBinaryConstraints() {
        LinkedHashSet<BinaryTuple> tuples;
        BinaryConstraint binaryConstraint;
        BinaryConstraintStorage cs = new BinaryConstraintStorage();
        for (int i = 0; i < nary.size(); i++) {
            for (int j = i + 1; j < nary.size(); j++) {
                NaryConstraint left = nary.get(i);
                NaryConstraint right = nary.get(j);
                tuples = left.getAllowedTuples(right);
                binaryConstraint = new BinaryConstraint(i, j, tuples);
                cs.map(i, j, binaryConstraint);
            }
        }
        return cs;
    }

    /**
     * Decodes the given {@link Solution} of a {@link BinaryCSP}
     * into an assignment of the original high-level variables.
     *
     * @param binarySolution the binary coded solution
     *
     * @return the decoded solution
     */
    public Assignment decodeSolution(Solution binarySolution) {
        Assignment decodedSolution = new Assignment();
        binarySolution.forEach((binaryVar, binaryVal) -> {
            NaryConstraint constraint = nary.get(binaryVar);
            Assignment value = constraint.getValue(binaryVal);
            value.forEach(decodedSolution::put);
        });
        return decodedSolution;
    }

    /**
     * Applies the given {@link BiConsumer} to all {@link BaseVariable}s.
     * It should accept a pair of name and {@link BaseVariable} instance.
     *
     * @param consumer the {@link BiConsumer} to apply
     */
    public void forEachVariable(BiConsumer<String, BaseVariable> consumer) {
        variables.forEach(consumer);
    }

    /** Prepares this {@link ECSProblem} for N-ary solving. */
    public void prepare() {
        applyUnary();
        for (NaryConstraint n : nary) {
            n.storeValidAssignments();
            String[] varNames = n.getVarNames();
            for (String varName : varNames) {
                BaseVariable var = variables.get(varName);
                var.addRelevant(n);
            }
        }

    }

    @Override
    public String toString() {
        StringBuilder vars = new StringBuilder("[");
        variables.forEach((name, var) -> vars.append(var));
        vars.append("]");

        StringBuilder naries = new StringBuilder("[");
        for (NaryConstraint naryConstraint : nary) {
            naries.append(naryConstraint.toString());
            naries.append("  (");
            naries.append(naryConstraint.getDomainSize());
            naries.append(")\n");
        }
        naries.append("]");
        return "ECSProblem{\n"
                + "variables=" + vars + ",\n"
                + "unary=" + unary + ",\n"
                + "nary=" + naries + "}";
    }

}

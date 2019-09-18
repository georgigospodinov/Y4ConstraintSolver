package nary.constraints;


import nary.ECSProblem;
import nary.types.BaseVariable;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.Objects;

/**
 * Represents a unary constraint.
 *
 * @author 150009974
 * @version 1.3
 */
public class UnaryConstraint {

    /**
     * Parses the given {@link String} as a {@link UnaryConstraint} declaration
     * and returns the created {@link UnaryConstraint}.
     * Returns null if the given {@link String} is not a valid
     * {@link UnaryConstraint} declaration.
     *
     * @param declaration the unary constraint declaration
     *
     * @return the created {@link UnaryConstraint}
     * or null if the given declaration is invalid
     */
    public static UnaryConstraint parse(String declaration) {
        Expression exp = new Expression(declaration);
        // A unary constraint declaration contains exactly 1 variable
        String[] args = exp.getMissingUserDefinedArguments();
        if (args.length != 1) {
            return null;
        }
        // Define argument, so that the instance can set the argument value
        exp.defineArgument(args[0], 0);
        return new UnaryConstraint(exp);
    }

    /** The constraint on the {@link BaseVariable} domain. */
    private Expression limit;

    /**
     * Creates a {@link UnaryConstraint} instance.
     *
     * @param exp the constraint on the {@link BaseVariable} domain
     */
    private UnaryConstraint(Expression exp) {
        limit = exp;
    }

    /**
     * Applies this {@link UnaryConstraint},
     * reducing the {@link BaseVariable}'s domain.
     *
     * @param problem the {@link ECSProblem} that
     *                contains the {@link BaseVariable} instance
     */
    public void apply(ECSProblem problem) {
        String arg = limit.getArgument(0).getArgumentName();
        BaseVariable var = problem.getVariable(arg);
        if (var == null) {
            String message = "Variable \"" + arg + "\" not declared: "
                    + limit.getExpressionString();
            throw new NullPointerException(message);
        }
        // Retain values that satisfy the expression.
        var.retainValues(i -> {
            limit.setArgumentValue(arg, i);
            return limit.calculate() == 1.0;
        });
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnaryConstraint)) {
            return false;
        }
        UnaryConstraint that = (UnaryConstraint) o;
        return limit.equals(that.limit);
    }

    @Override
    public String toString() {
        return "UnaryConstraint{"
                + "limit=\"" + limit.getExpressionString() + "\","
                + "}";
    }

}

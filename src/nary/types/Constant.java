package nary.types;

import nary.Utilities;
import org.mariuszgromada.math.mxparser.Expression;

/**
 * Represents a constant in the high-level constraint satisfaction problem.
 *
 * @author 150009974
 * @version 2.1
 */
public class Constant {

    /**
     * Parses the given {@link String} as a {@link Constant} declaration
     * and returns the created {@link Constant}.
     *
     * @param declaration the line to parse
     *
     * @return the created {@link Constant} or null if invalid declaration
     *
     * @throws IllegalArgumentException if the given declaration is invalid
     */
    public static Constant parse(String declaration) {
        String[] nameValue = Utilities.splitNameAndValues(declaration);

        // Name starts after 'const'
        String name = nameValue[0].substring(Utilities.CONSTANT.length()).trim();

        // Declaration cannot have any variables.
        Expression exp = new Expression(nameValue[1].trim());
        double calc = exp.calculate();
        if (Double.isNaN(calc)) {
            String msg = "Could not calculate " + exp.getExpressionString();
            throw new IllegalArgumentException(msg);
        }
        int value = (int) calc;
        return new Constant(name, value);
    }

    /** The name of this {@link Constant}. */
    private String name;

    /** The value of this {@link Constant}. */
    private int value;

    /**
     * Creates a {@link Constant} with the given name and value.
     *
     * @param name  the name of the constant
     * @param value the value of the constant
     */
    private Constant(String name, int value) {
        this.name = name;
        this.value = value;
    }

    /** @return the name of this {@link Constant} */
    public String getName() {
        return name;
    }

    /** @return the value of this {@link Constant} */
    public int getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Constant)) {
            return false;
        }
        Constant constant = (Constant) o;
        return value == constant.value
                && name.equals(constant.name);
    }

    @Override
    public String toString() {
        return "Constant{" + name + "=" + value + "}";
    }

}

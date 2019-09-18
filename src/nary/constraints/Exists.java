package nary.constraints;

import nary.Utilities;

import java.util.ArrayList;

/**
 * Provides parsing of exists constraints.
 * Only one line after the exists declaration is allowed.
 *
 * @author 150009974
 * @version 1.0
 */
public class Exists {

    /**
     * Parses the given {@link String} as an {@link Exists} declaration
     * and returns the created constraint declaration.
     *
     * @param declaration   the line to parse
     * @param subconstraint the constraint that gets multiplied
     *
     * @return the created constraint declarations
     *
     * @throws IllegalArgumentException if the given declaration is invalid
     */
    public static String parse(String declaration, String subconstraint) {
        String[] nameValues = Utilities.splitNameAndValues(declaration);
        // Name starts after 'var'
        String name = nameValues[0].substring(Utilities.EXISTS.length()).trim();
        int[] range = Utilities.parseMinMax(nameValues[1]);
        int first = range[Utilities.MIN_INDEX];
        int last = range[Utilities.MAX_INDEX];
        return createConstraints(subconstraint, name, first, last);
    }


    /**
     * Iterates the values in the given range,
     * multiplying the given subconstraint,
     * and joining all the copies in a large disjunction.
     * The given subconstraint is copied once for each value
     * and the occurrences of the specified variable in it are replaced
     * with the value.
     *
     * @param subconstraint the constraint to multiply
     * @param name          the name of the variable that is to be iterated
     * @param first         the first value in the range (inclusive)
     * @param last          the last value in the range (inclusive)
     *
     * @return the created disjunction
     */
    public static String createConstraints(String subconstraint, String name, int first, int last) {
        ArrayList<String> constraints = new ArrayList<>();
        for (int i = first; i <= last; i++) {
            String declaration = createCopy(subconstraint, name, i);
            constraints.add(declaration);
        }

        return String.join(" \\/ ", constraints);
    }

    /**
     * Creates a copy of the given constraint,
     * replacing the appearances of the given variable
     * with the given value.
     *
     * @param c   the constraint to copy
     * @param var the variable to remove
     * @param i   the value to place
     *
     * @return the created copy
     */
    private static String createCopy(String c, String var, int i) {
        // Match only var
        c = c.replaceAll("^" + var + "$", String.valueOf(i));
        // Math at start
        c = c.replaceAll("^" + var + " ", i + " ");
        // Match in middle
        c = c.replaceAll(" " + var + " ", " " + i + " ");
        // Match at end
        c = c.replaceAll(" " + var + "$", " " + i);

        // match in matrix access
        c = c.replaceAll("\\[" + var + "]", "[" + i + "]");
        c = c.replaceAll("\\[" + var + " ", "[" + i + " ");
        c = c.replaceAll(" " + var + "]", " " + i + "]");
        return c;
    }

}

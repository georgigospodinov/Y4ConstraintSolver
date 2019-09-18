package nary;

import org.mariuszgromada.math.mxparser.Expression;

/**
 * Contains common parsing utilities.
 *
 * @author 150009974
 * @version 1.3
 */
public interface Utilities {

    /** A separator between the name and the value in a declaration. */
    String NAME_VALUE_SEPARATOR = ":";

    /** The start of a constant declaration. */
    String CONSTANT = "const";

    /** The start of a variable declaration. */
    String VARIABLE = "var";

    /** The start of a matrix declaration. */
    String MATRIX = "matrix";

    /** The start of an all-different constraint. */
    String ALL_DIFF = "allDiff";

    /** The start of a for-all constraint. */
    String FOR_ALL = "forAll";

    /** The start of an exists constraint. */
    String EXISTS = "exists";

    /** The separator between the values in a declaration/domain. */
    String VALUES_SEPARATOR = ",";

    /** The separator between domains. */
    String DOMAIN_SEPARATOR = ";";

    /**
     * The index of the minimum value in the array returned by
     * {@link Utilities#parseMinMax(String)}.
     */
    int MIN_INDEX = 0;

    /**
     * The index of the maximum value in the array returned by
     * {@link Utilities#parseMinMax(String)}.
     */
    int MAX_INDEX = 1;

    /**
     * Splits the name and the value of a declaration.
     * This method throws an {@link IllegalArgumentException} if
     * the there aren't exactly two {@link String}s after the split.
     *
     * @param declaration the declaration to split
     *
     * @return the split name (index 0) and value (index 1)
     *
     * @throws IllegalArgumentException if there are isn't
     *                                  exactly one name and one value
     */
    static String[] splitNameAndValues(String declaration) {
        String[] nameValues = declaration.split(NAME_VALUE_SEPARATOR);
        if (nameValues.length != 2) {
            String msg = "Exactly one \"" + NAME_VALUE_SEPARATOR
                    + "\" must separate the name from the value in "
                    + declaration.trim();
            throw new IllegalArgumentException(msg);
        }
        return nameValues;
    }

    /**
     * Parses a minimum and a maximum value from the given {@link String}.
     * It is expected that the two values are separated
     * with a {@link Utilities#VALUES_SEPARATOR}.
     * Returns the parsed values in an integer array of length 2.
     * The 0th element is the minimum value.
     * The 1th element is the maximum value.
     *
     * @param raw the String representation to parse
     *
     * @return the min and max values
     *
     * @throws IllegalArgumentException if the values could not be parsed
     */
    static int[] parseMinMax(String raw) {
        String[] rawVals = raw.split(VALUES_SEPARATOR);
        if (rawVals.length != 2) {
            String msg = "Exactly one \"" + VALUES_SEPARATOR
                    + "\" must separate the two values in " + raw.trim();
            throw new IllegalArgumentException(msg);
        }

        Expression minExp = new Expression(rawVals[0].trim());
        double calc = minExp.calculate();
        if (Double.isNaN(calc)) {
            String msg = "Could not calculate " + minExp.getExpressionString();
            throw new IllegalArgumentException(msg);
        }
        int min = (int) calc;

        Expression maxExp = new Expression(rawVals[1].trim());
        calc = maxExp.calculate();
        if (Double.isNaN(calc)) {
            String msg = "Could not calculate " + maxExp.getExpressionString();
            throw new IllegalArgumentException(msg);
        }
        int max = (int) calc;

        return new int[]{min, max};
    }

}

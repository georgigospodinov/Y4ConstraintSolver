package nary.constraints;

import nary.Utilities;
import nary.types.BaseVariable;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Represents an All Different constraint.
 *
 * @author 150009974
 * @version 1.3
 */
public class AllDiff {

    /** The declared {@link BaseVariable}s in the ecsp file. */
    private LinkedHashMap<String, BaseVariable> declared;

    /**
     * Creates an {@link AllDiff} constraint that will reference
     * the given declared {@link BaseVariable}s
     * when constructing the appropriate {@link NaryConstraint}s.
     *
     * @param declared a map from variable name to {@link BaseVariable}s
     */
    public AllDiff(LinkedHashMap<String, BaseVariable> declared) {
        this.declared = declared;
    }

    /**
     * Parses an all different constraint.
     * A {@link NaryConstraint} is created for each pair of given variables.
     * It says that those two variables must be different.
     * All created {@link NaryConstraint}s are returned in a {@link LinkedList}.
     * Returns null if the given {@link String} is not a valid
     * {@link AllDiff} declaration.
     *
     * @param declaration the line to parse
     *
     * @return the created {@link NaryConstraint}s
     * or null if invalid declaration
     *
     * @throws IllegalArgumentException if a variable is not recognized
     */
    public LinkedList<NaryConstraint> parse(String declaration) {
        String raw = declaration.substring(Utilities.ALL_DIFF.length());
        ArrayList<BaseVariable> matched = getMatchingVariables(raw);
        return createDifferenceConstraints(matched);
    }

    /**
     * Retrieves all {@link BaseVariable}s listed in the given {@link String}.
     * The raw {@link String} should list all variable names,
     * separated by commas.
     * Matrix accesses with specific indexes are resolved to variable names.
     * Matrix accesses with '..' are replaced with all matching variables.
     *
     * @param raw the comma-separated variable names
     *
     * @return the matched {@link BaseVariable}s
     */
    private ArrayList<BaseVariable> getMatchingVariables(String raw) {
        String[] tokens = raw.split(Utilities.VALUES_SEPARATOR);
        ArrayList<BaseVariable> matched = new ArrayList<>();
        for (String token : tokens) {
            String namePattern = getVarMatch(token);
            if (addMatches(matched, namePattern)) {
                String msg = "Could not recognize: " + token;
                throw new IllegalArgumentException(msg);
            }
        }
        return matched;
    }

    /**
     * Searches the {@link AllDiff#declared} variables for any that match
     * the given pattern and adds them to the given {@link ArrayList}.
     * Returns true if and only if no match was found.
     * In other words, the matched {@link ArrayList} remained the same.
     *
     * @param matched     the list of matched {@link BaseVariable}s to expand
     * @param namePattern the pattern to search for
     *
     * @return true iff NO match was found
     */
    private boolean addMatches(ArrayList<BaseVariable> matched, String namePattern) {
        boolean noMatchFound = true;
        for (BaseVariable var : declared.values()) {
            if (var.getName().matches(namePattern)) {
                matched.add(var);
                noMatchFound = false;
            }

        }
        return noMatchFound;
    }

    /**
     * Creates and returns {@link NaryConstraint}s to represent
     * this {@link AllDiff} constraint.
     * A {@link NaryConstraint} is formed for every pair of matched {@link BaseVariable}s.
     * It says that the two must be different.
     * Returns all created {@link NaryConstraint}s in a {@link LinkedList}.
     *
     * @param matched the {@link BaseVariable}s matched by the tokens
     *
     * @return the created {@link NaryConstraint}s
     *
     * @see AllDiff#getMatchingVariables(String)
     */
    private LinkedList<NaryConstraint> createDifferenceConstraints(ArrayList<BaseVariable> matched) {
        LinkedList<NaryConstraint> constraints = new LinkedList<>();
        for (int i = 0; i < matched.size(); i++) {
            for (int j = i + 1; j < matched.size(); j++) {
                String left = matched.get(i).getName();
                String right = matched.get(j).getName();
                String declaration = left + " != " + right;
                NaryConstraint diff = NaryConstraint.parse(declaration);
                constraints.addLast(diff);
            }
        }
        return constraints;
    }

    /**
     * Creates and returns a regex that can match all variable names
     * specified by the given token.
     * If the token is a single variable name,
     * then only that variable will be matched.
     * Otherwise, the token must be a matrix access, in which case,
     * specific indexes are preserved, while indexing with '..' will
     * cause the regex to match all possible indexes.
     *
     * @param token the token to parse
     *
     * @return the regex that matches variables
     */
    private String getVarMatch(String token) {
        StringBuilder sb = nameMatch(token);
        int start = token.indexOf("[");
        int end = token.indexOf("]");
        while (start != -1 && end != -1) {
            String access = token.substring(start + 1, end).trim();
            indexMatch(sb, access);

            // Cut the processed part of the token
            token = token.substring(end + 1);
            start = token.indexOf("[");
            end = token.indexOf("]");
        }
        // Match end of variable name
        sb.append("$");
        return sb.toString();
    }

    /**
     * Creates and returns a {@link StringBuilder} that contains
     * a regex that matches the name of the variable in the given token.
     * If the token is a matrix access token,
     * the regex will match only the name of the matrix.
     * Otherwise, it will match the whole token (the token must be a variable).
     *
     * @param token the token to parse
     *
     * @return the instantiated {@link StringBuilder} containing the regex
     */
    private StringBuilder nameMatch(String token) {
        int matrixAccessStart = token.indexOf("[");
        // Match start of variable name
        StringBuilder sb = new StringBuilder("^");
        String name;
        if (matrixAccessStart == -1) {
            // Not a matrix access, parse the whole token as a name.
            name = Pattern.quote(token.trim());
        } else {
            // Matrix access, parse the start of the token as a name.
            name = Pattern.quote(token.substring(0, matrixAccessStart).trim());
        }
        sb.append(name);
        return sb;
    }

    /**
     * Extends the regex in the given {@link StringBuilder}
     * to match the given matrix access.
     * If this is access to a specific index, only that index is matched.
     * If its access to '..', then all indexes are matched.
     *
     * @param sb     the {@link StringBuilder} with the regex to extend
     * @param access the matrix access
     */
    private void indexMatch(StringBuilder sb, String access) {
        sb.append("_");
        if (access.equals("..")) {
            // Match all indexes
            sb.append("-?\\d+");
        } else {
            // Match specific index
            Expression exp = new Expression(access.trim());
            int index = (int) exp.calculate();
            sb.append(index);
        }
        sb.append("_");
    }

}

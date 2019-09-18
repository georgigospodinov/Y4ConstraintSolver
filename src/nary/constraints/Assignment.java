package nary.constraints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Represents an assignment of some {@link nary.types.BaseVariable}s
 * in a {@link NaryConstraint}.
 * Maps a variable name to a value.
 *
 * @author 150009974
 * @version 1.0
 */
public class Assignment extends LinkedHashMap<String, Integer> {

    /**
     * Creates and returns a deep copy of this {@link Assignment}.
     *
     * @return a deep copy of this {@link Assignment}
     */
    public Assignment deepCopy() {
        Assignment copy = new Assignment();
        this.forEach(copy::put);
        return copy;
    }

    /**
     * Determines if this {@link Assignment} is compatible
     * with the given {@link Assignment}.
     * That is, if a variable appears in both {@link Assignment}s,
     * then it must have the same value in both.
     *
     * @param that the {@link Assignment} to check against
     *
     * @return true iff this {@link Assignment}
     * is compatible with that {@link Assignment}
     */
    public boolean isCompatibleWith(Assignment that) {
        Set<String> vars = this.keySet();
        for (String var : vars) {
            if (that.containsKey(var)) {
                Integer thisVal = this.get(var);
                Integer thatVal = that.get(var);
                if (!thatVal.equals(thisVal)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[\n");
        ArrayList<String> names = new ArrayList<>(keySet());
        Collections.sort(names);
        for (String name : names) {
            int val = this.get(name);
            sb.append(asMatrixAccess(name));
            sb.append(" = ");
            sb.append(val);
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Transforms the given variable name into matrix access.
     *
     * @return the matrix access representation
     */
    private String asMatrixAccess(String varName) {
        int start;
        int end;
        StringBuilder nameEditor = new StringBuilder(varName);
        // Starting in the back of the name,
        // replace "_<index>_" with "[<index>]"
        while (true) {
            end = nameEditor.lastIndexOf("_");
            // If the "_" is not at the end, then no more matrix accesses
            if (end == nameEditor.length() - 1
                    || nameEditor.charAt(end + 1) == '[') {
                nameEditor.setCharAt(end, ']');

                start = nameEditor.lastIndexOf("_");
                nameEditor.setCharAt(start, '[');
            } else {
                return nameEditor.toString();
            }
        }
    }

}

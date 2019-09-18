package binary.types;

import java.util.LinkedHashSet;

/**
 * Represents a binary constraint between two variables.
 *
 * @version 3.0
 */
public final class BinaryConstraint {

    /** The first variable of this constraint. */
    private int var1;

    /** The second variable of this constraint. */
    private int var2;

    /** The {@link BinaryTuple}s that this {@link BinaryConstraint} permits. */
    private LinkedHashSet<BinaryTuple> tuples;

    /**
     * Creates a {@link BinaryConstraint} for the given variables.
     *
     * @param firstVar     the index of the first variable
     * @param secondVar    the index of the second variable
     * @param binaryTuples the {@link BinaryTuple}s that
     *                     this {@link BinaryConstraint} permits
     */
    public BinaryConstraint(int firstVar, int secondVar,
                            LinkedHashSet<BinaryTuple> binaryTuples) {
        var1 = firstVar;
        var2 = secondVar;
        tuples = binaryTuples;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BinaryConstraint)) {
            return false;
        }
        BinaryConstraint other = (BinaryConstraint) obj;
        return this.var1 == other.var1 && this.var2 == other.var2;
    }

    /***
     * Creates and returns a {@link String} representation
     * of this {@link BinaryConstraint}.
     *
     * @return the {@link String} representing this {@link BinaryConstraint}
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String declaration = "c(" + var1 + ", " + var2 + ")\n";
        result.append(declaration);
        for (BinaryTuple tuple : tuples) {
            result.append(tuple);
            result.append("\n");
        }
        return result.toString();
    }

    /**
     * Retrieves the set of supported values for the first variable,
     * given a domain of values for the second variable.
     *
     * @param domain the domain of values of the second variable
     *
     * @return the supported values for the first variable
     */
    public LinkedHashSet<Integer> getFirstSupported(LinkedHashSet<Integer> domain) {
        LinkedHashSet<Integer> supported = new LinkedHashSet<>();
        for (BinaryTuple tuple : tuples) {
            if (domain.contains(tuple.getVal2())) {
                supported.add(tuple.getVal1());
            }
        }
        return supported;
    }

    /**
     * Retrieves the set of supported values for the second variable,
     * given a domain of values for the first variable.
     *
     * @param domain the domain of values of the first variable
     *
     * @return the supported values for the second variable
     */
    public LinkedHashSet<Integer> getSecondSupported(LinkedHashSet<Integer> domain) {
        LinkedHashSet<Integer> supported = new LinkedHashSet<>();
        for (BinaryTuple tuple : tuples) {
            if (domain.contains(tuple.getVal1())) {
                supported.add(tuple.getVal2());
            }
        }
        return supported;
    }

}

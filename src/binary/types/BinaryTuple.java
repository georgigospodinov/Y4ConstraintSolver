package binary.types;

import java.util.Objects;

/**
 * Represents a binary tuple of integers.
 *
 * @version 2.0
 */
public final class BinaryTuple {

    /** The first value in the tuple. */
    private int val1;

    /** The second value in the tuple. */
    private int val2;

    /**
     * Creates a new {@link BinaryTuple} with the specified values.
     *
     * @param v1 the first value
     * @param v2 the second value
     */
    public BinaryTuple(int v1, int v2) {
        val1 = v1;
        val2 = v2;
    }

    /**
     * Checks if the given values match the values of this {@link BinaryTuple}.
     *
     * @param v1 the first value
     * @param v2 the second value
     *
     * @return true if this {@link BinaryTuple} matches the given values
     */
    public boolean matches(int v1, int v2) {
        return val1 == v1 && val2 == v2;
    }

    /** @return the first value in this {@link BinaryTuple} */
    public int getVal1() {
        return val1;
    }

    /** @return the second value in this {@link BinaryTuple} */
    public int getVal2() {
        return val2;
    }

    /**
     * Calculates and returns the hash code of this {@link BinaryTuple}
     * by hashing the values of this {@link BinaryTuple}.
     *
     * @return the hash code of this {@link BinaryTuple}
     *
     * @see Objects#hash(Object...)
     */
    @Override
    public int hashCode() {
        return Objects.hash(val1, val2);
    }

    /**
     * Checks if this {@link BinaryTuple} is equal to the given object.
     * This is true if and only if
     * the given object is a {@link BinaryTuple} with the same values.
     *
     * @param obj the {@link Object} to compare to
     *
     * @return true iff obj is a {@link BinaryTuple} with the same values
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BinaryTuple)) {
            return false;
        }
        BinaryTuple other = (BinaryTuple) obj;
        return this.val1 == other.val1 && this.val2 == other.val2;
    }

    /**
     * Creates and returns a {@link String} representation
     * of this {@link BinaryTuple}.
     *
     * @return the {@link String} representation of this {@link BinaryTuple}
     */
    @Override
    public String toString() {
        return "<" + val1 + ", " + val2 + ">";
    }

}

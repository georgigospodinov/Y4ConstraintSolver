package binary.types;

import binary.BinaryCSP;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Represents a variable in a constraint problem.
 *
 * @author 150009974
 * @version 4.1
 */
public abstract class Variable {

    /** The currently assigned value to this variable. */
    private Integer value = null;

    /** The domain of assignable values. */
    protected Collection<Integer> domain;

    /** The id of this {@link Variable} in the {@link BinaryCSP}. */
    private int id;

    /**
     * Creates a {@link Variable} with the given index as an id.
     * The given {@link Collection} of {@link Integer}s is used as a domain.
     * Implementations should provide their own instances.
     *
     * @param index  the index of this {@link Variable} in the {@link BinaryCSP}
     * @param values the values that this {@link Variable} can take
     */
    protected Variable(int index, Collection<Integer> values) {
        id = index;
        domain = values;
    }

    /** @return the id of this {@link Variable} */
    public int getId() {
        return id;
    }

    /**
     * Assigns the specified value to this {@link Variable}.
     *
     * @param val the new value to assign
     */
    public void assign(int val) {
        value = val;
    }

    /** Removes the currently assigned value. */
    public void unassign() {
        value = null;
    }

    /**
     * Removes the specified value from this {@link Variable}'s domain.
     *
     * @param val the value to remove
     */
    public void removeFromDomain(int val) {
        domain.remove(val);
    }

    /**
     * Adds the specified value to this {@link Variable}'s domain.
     *
     * @param val the value to add
     */
    public void addToDomain(int val) {
        domain.add(val);
    }

    /** @return the size of the domain of this {@link Variable} */
    public int getDomainSize() {
        return domain.size();
    }

    /**
     * Determines whether the domain of this {@link Variable}
     * contains the given value.
     *
     * @param val the value to look for
     *
     * @return true iff the given value is in this {@link Variable}'s domain
     */
    public boolean domainContains(int val) {
        return domain.contains(val);
    }

    /**
     * Returns the current domain of this {@link Variable}.
     * If this {@link Variable} is assigned,
     * the returned domain contains only the assigned value.
     *
     * @return a deep copy of this {@link Variable}'s domain
     * or the currently assigned value
     */
    public LinkedHashSet<Integer> getDomain() {
        if (value == null) {
            return new LinkedHashSet<>(domain);
        } else {
            LinkedHashSet<Integer> d = new LinkedHashSet<>();
            d.add(value);
            return d;
        }
    }

    /**
     * Checks if this {@link Variable} is consistent.
     * That is, it has at least one value in its domain.
     *
     * @return true iff this Variable has at least one value in its domain
     */
    public boolean isConsistent() {
        return !domain.isEmpty();
    }

    /** @return the next value to branch on */
    public abstract int getNextVal();

    /**
     * Returns the hash code of this {@link Variable}
     * which is the {@link Variable}'s {@link Variable#id}.
     *
     * @return the hash code of this {@link Variable}
     */
    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Variable)) {
            return false;
        }
        Variable other = (Variable) obj;
        return this.id == other.id;
    }

    /**
     * Creates and returns a {@link String} representing this {@link Variable}.
     *
     * @return the {@link String} representing this {@link Variable}
     */
    @Override
    public String toString() {
        return "Var " + getId() + ": " + domain + ", currently=" + value;
    }

    /** @return the currently assigned value of this {@link Variable} */
    public int getValue() {
        return value;
    }

    /**
     * Removes and returns all values from the domain of this {@link Variable}
     * that do not appear in the given set.
     *
     * @param supported the set of values to preserve
     *
     * @return the {@link LinkedHashSet} of all removed values
     */
    public LinkedHashSet<Integer> retainValues(LinkedHashSet<Integer> supported) {
        LinkedHashSet<Integer> removed = new LinkedHashSet<>();
        Iterator<Integer> iterator = domain.iterator();
        while (iterator.hasNext()) {
            int value = iterator.next();
            if (!supported.contains(value)) {
                iterator.remove();
                removed.add(value);
            }
        }
        return removed;
    }

}

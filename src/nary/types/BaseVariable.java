package nary.types;

import nary.Utilities;
import nary.constraints.NaryConstraint;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a variable in the high-level constraint satisfaction problem.
 *
 * @author 150009974
 * @version 1.6
 */
public class BaseVariable {

    /**
     * Parses the given {@link String} as a {@link BaseVariable} declaration
     * and returns the created {@link BaseVariable}.
     *
     * @param declaration the line to parse
     *
     * @return the created {@link BaseVariable}
     *
     * @throws IllegalArgumentException if the given declaration is invalid
     */
    public static BaseVariable parse(String declaration) {
        String[] nameValues = Utilities.splitNameAndValues(declaration);
        // Name starts after 'var'
        String name = nameValues[0].substring(Utilities.VARIABLE.length()).trim();
        int[] minMax = Utilities.parseMinMax(nameValues[1]);
        int min = minMax[Utilities.MIN_INDEX];
        int max = minMax[Utilities.MAX_INDEX];
        return new BaseVariable(name, min, max);
    }

    /** The name of this {@link BaseVariable}. */
    private String name;

    /** The domain of allowed values. */
    private HashSet<Integer> domain = new HashSet<>();

    private Integer value = null;

    private LinkedHashSet<NaryConstraint> relevant = new LinkedHashSet<>();

    /**
     * Creates a {@link BaseVariable} with the given name, min, and max values.
     *
     * @param name the name of the variable
     * @param min  the minimum value it can take
     * @param max  the maximum value it can take
     */
    // Package private constructor. Matrix class needs to create BaseVariables.
    BaseVariable(String name, int min, int max) {
        this.name = name;
        for (int i = min; i <= max; i++) {
            domain.add(i);
        }
    }

    /** @return the name of this {@link BaseVariable} */
    public String getName() {
        return name;
    }

    /** @return the domain of allowed values */
    public LinkedHashSet<Integer> getDomain() {
        if (value == null) {
            return new LinkedHashSet<>(domain);
        } else {
            LinkedHashSet<Integer> d = new LinkedHashSet<>();
            d.add(value);
            return d;
        }
    }

    /** @return the size of the domain of this {@link BaseVariable} */
    public int getDomainSize() {
        return domain.size();
    }

    /**
     * Assigns the specified value to this {@link BaseVariable}.
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
     * Removes the specified value from this {@link BaseVariable}'s domain.
     *
     * @param val the value to remove
     */
    public void removeFromDomain(int val) {
        domain.remove(val);
    }

    /**
     * Adds the specified value to this {@link BaseVariable}'s domain.
     *
     * @param val the value to add
     */
    public void addToDomain(int val) {
        domain.add(val);
    }

    /**
     * Checks if this {@link BaseVariable} is consistent.
     * That is, it has at least one value in its domain.
     *
     * @return true iff this Variable has at least one value in its domain
     */
    public boolean isConsistent() {
        return !domain.isEmpty();
    }

    /** @return the next value to branch on */
    public int getNextValue() {
        return domain.iterator().next();
    }

    /** @return the currently assigned value of this {@link BaseVariable} */
    public int getValue() {
        return value;
    }

    /**
     * Stores the given {@link NaryConstraint} as being "relevant".
     * Relevant constraints are ones in which this {@link BaseVariable} appears.
     *
     * @param constraint the relevant {@link NaryConstraint}
     */
    public void addRelevant(NaryConstraint constraint) {
        relevant.add(constraint);
    }

    /**
     * Creates and returns a set containing all {@link NaryConstraint}s
     * in which both this and the given {@link BaseVariable} appear.
     *
     * @param that the other {@link BaseVariable}
     *
     * @return the common {@link NaryConstraint}s
     */
    public LinkedHashSet<NaryConstraint> getCommonConstraints(BaseVariable that) {
        if (that.equals(this)) {
            return null;
        }
        LinkedHashSet<NaryConstraint> common = new LinkedHashSet<>(relevant);
        common.retainAll(that.relevant);
        if (common.isEmpty()) {
            return null;
        }
        return common;
    }

    /**
     * Removes and returns all values from the domain of this {@link BaseVariable}
     * that do not satisfy the specified condition.
     * The condition {@link Function} is invoked
     * on each value in this {@link BaseVariable}'s domain,
     * and when it returns false, the value is removed.
     *
     * @param condition the condition {@link Function} to apply
     *
     * @return the {@link LinkedHashSet} of all removed values
     */
    public LinkedHashSet<Integer> retainValues(Function<Integer, Boolean> condition) {
        LinkedHashSet<Integer> removed = new LinkedHashSet<>();
        Iterator<Integer> iterator = domain.iterator();
        while (iterator.hasNext()) {
            int value = iterator.next();
            if (!condition.apply(value)) {
                iterator.remove();
                removed.add(value);
            }
        }
        return removed;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseVariable)) {
            return false;
        }
        BaseVariable that = (BaseVariable) o;
        return name.equals(that.name);
    }

    @Override
    public String toString() {
        return "{\"" + name + "\"" + "," + domain.size() + "}";
    }

}

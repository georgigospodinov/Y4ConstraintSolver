package binary.algorithms;

import binary.BinaryCSP;
import binary.types.BinaryConstraint;
import binary.types.Variable;
import main.Logging;

import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * Represents an arc from one {@link Variable} to another.
 *
 * @author 150009974
 * @version 1.3
 */
public class Arc {

    /** The {@link Variable} whose domain depends on the supporter. */
    private Variable dependent;

    /** The {@link Variable} that supports values for the dependent. */
    private Variable supporter;

    /**
     * Creates an {@link Arc} between the given {@link Variable}s.
     *
     * @param dep the dependent {@link Variable}
     * @param sup the supporter {@link Variable}
     */
    public Arc(Variable dep, Variable sup) {
        dependent = dep;
        supporter = sup;
    }

    /** @return the {@link Variable} whose domain depends on the supporter */
    public Variable getDependent() {
        return dependent;
    }

    /** @return the {@link Variable} that supports values for the dependent */
    public Variable getSupporter() {
        return supporter;
    }

    /**
     * Prunes and returns values from the {@link Arc#dependent}'s domain
     * based on the {@link Arc#supporter}'s domain.
     *
     * @param csp the {@link BinaryCSP} to which the {@link Variable}s belong
     *
     * @return the performed {@link Prune} on the dependent {@link Variable}
     */
    public Prune prune(BinaryCSP csp) {
        LinkedHashSet<Integer> domain = supporter.getDomain();
        LinkedHashSet<Integer> supported;
        LinkedHashSet<Integer> removed;
        BinaryConstraint cds = csp.getBinaryConstraint(dependent, supporter);
        BinaryConstraint csd = csp.getBinaryConstraint(supporter, dependent);
        if (cds != null) {
            if (Logging.logArcRevisionConstraints()) {
                System.out.println("\twith constraint: " + cds);
            }
            supported = cds.getFirstSupported(domain);
        } else {
            if (Logging.logArcRevisionConstraints()) {
                System.out.println("\twith constraint: " + csd);
            }
            supported = csd.getSecondSupported(domain);
        }
        removed = dependent.retainValues(supported);
        return new Prune(dependent, removed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dependent, supporter);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Arc)) {
            return false;
        }
        Arc other = (Arc) obj;
        return this.dependent.equals(other.dependent)
                && this.supporter.equals(other.supporter);
    }

    @Override
    public String toString() {
        return "(" + dependent + ", " + supporter + ")";
    }

}

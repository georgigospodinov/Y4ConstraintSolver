package binary.algorithms;

import binary.BinaryCSP;
import binary.types.Variable;
import main.Logging;

import java.util.LinkedHashSet;

/**
 * An implementation of the Maintaining Arc Consistency
 * constraint solving algorithm, specifically for {@link BinaryCSP}.
 *
 * @author 150009974
 * @version 1.3
 */
public class MaintainingArcConsistency extends BinarySolving {

    /**
     * Creates a {@link MaintainingArcConsistency} instance
     * to solve the given {@link BinaryCSP}.
     *
     * @param csp the {@link BinaryCSP} to solve
     */
    public MaintainingArcConsistency(BinaryCSP csp) {
        super(csp);
    }

    @Override
    protected boolean revise(Variable var, LinkedHashSet<Prune> pruned) {
        LinkedHashSet<Arc> queue = initialiseQueue(var);
        while (!queue.isEmpty()) {
            Arc a = nextArc(queue);
            incrementRevisionCounter();
            Prune entry = a.prune(getProblem());
            pruned.add(entry);

            Variable dep = a.getDependent();
            // Only this variable has changed, therefore check it alone.
            if (!dep.isConsistent()) {
                return false;
            }

            if (!entry.isEmpty()) {
                // The dependent has changed,
                // so all Variables that depend on it must be updated.
                for (Variable h : getFuture()) {
                    if (!h.equals(a.getSupporter())) {
                        if (getProblem().existsConstraint(h, dep)) {
                            queue.add(new Arc(h, dep));
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Retrieves the next {@link Arc} from the queue.
     * The queue is a {@link LinkedHashSet},
     * so elements are iterated and removed in insertion order.
     *
     * @param queue the queue of {@link Arc}s
     *
     * @return the next {@link Arc} to revise
     */
    private Arc nextArc(LinkedHashSet<Arc> queue) {
        Arc a = queue.iterator().next();
        queue.remove(a);
        if (Logging.logArcRevision()) {
            System.out.println("Revising arc: " + a);
        }
        return a;
    }

    /**
     * Initialises a {@link LinkedHashSet} of {@link Arc}s as a queue
     * of {@link Arc}s that need to be revised.
     * The returned queue contains all {@link Arc}s where
     * the given {@link Variable} is a supporter.
     *
     * @param var the supporter {@link Variable}
     *
     * @return the queue of {@link Arc}s that need to be revised
     */
    private LinkedHashSet<Arc> initialiseQueue(Variable var) {
        LinkedHashSet<Arc> queue = new LinkedHashSet<>();
        for (Variable f : getFuture()) {
            if (getProblem().existsConstraint(f, var)) {
                queue.add(new Arc(f, var));
            }
        }
        return queue;
    }

}

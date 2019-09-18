package binary.algorithms;

import binary.BinaryCSP;
import binary.types.Variable;
import main.Logging;

import java.util.LinkedHashSet;

/**
 * An implementation of the Forward Checking constraint solving algorithm
 * specifically for {@link BinaryCSP}s.
 *
 * @author 150009974
 * @version 3.2
 */
public class ForwardChecking extends BinarySolving {

    /**
     * Creates a {@link ForwardChecking} instance
     * to solve the given {@link BinaryCSP}.
     *
     * @param csp the {@link BinaryCSP} to solve
     */
    public ForwardChecking(BinaryCSP csp) {
        super(csp);
    }

    @Override
    protected boolean revise(Variable var, LinkedHashSet<Prune> prunes) {
        for (Variable f : getFuture()) {
            if (!getProblem().existsConstraint(f, var)) {
                continue;
            }
            Arc a = new Arc(f, var);
            incrementRevisionCounter();
            if (Logging.logArcRevision()) {
                System.out.println("Revising arc: " + a);
            }
            Prune entry = a.prune(getProblem());
            prunes.add(entry);
            // Only this variable has changed, therefore check it alone.
            if (!f.isConsistent()) {
                return false;
            }
        }
        return true;
    }

}

package nary.algorithms;

import nary.ECSProblem;
import nary.constraints.NaryConstraint;
import nary.types.BaseVariable;

import java.util.LinkedHashSet;

/**
 * An implementation of the Maintaining Arc Consistency
 * constraint solving algorithm, specifically for {@link nary.ECSProblem}s.
 *
 * @author 150009974
 * @version 1.2
 */
public class NaryMaintainingArcConsistency extends NarySolving {

    /**
     * Creates a {@link NaryMaintainingArcConsistency} instance
     * to solve the given {@link ECSProblem}.
     *
     * @param problem the {@link ECSProblem} to solve
     */
    public NaryMaintainingArcConsistency(ECSProblem problem) {
        super(problem);
    }

    @Override
    protected boolean revise(BaseVariable var, LinkedHashSet<BaseVarPrune> prunes) {
        LinkedHashSet<BaseVarArc> queue = initQueue(var);
        while (!queue.isEmpty()) {
            BaseVarArc a = queue.iterator().next();
            queue.remove(a);
            incrementRevisionCounter();
            BaseVarPrune entry = a.prune(getPast());
            prunes.add(entry);

            BaseVariable dep = a.getDependent();
            if (!dep.isConsistent()) {
                return false;
            }

            if (!entry.isEmpty()) {
                for (String hName : getFuture()) {
                    BaseVariable h = getProblem().getVariable(hName);
                    if (!h.equals(a.getSupporter())) {
                        LinkedHashSet<NaryConstraint> common = h.getCommonConstraints(dep);
                        if (common == null) {
                            continue;
                        }
                        queue.add(new BaseVarArc(h, dep, common));
                    }
                }
            }
        }
        return true;
    }

    /**
     * Initialises a {@link LinkedHashSet} of {@link BaseVarArc}s as a queue
     * of {@link BaseVarArc}s that need to be revised.
     * The returned queue contains all {@link BaseVarArc}s where
     * the given {@link BaseVariable} is a supporter.
     *
     * @param var the supporter {@link BaseVariable}
     *
     * @return the wueue of {@link BaseVarArc}s that need to be revised
     */
    private LinkedHashSet<BaseVarArc> initQueue(BaseVariable var) {
        LinkedHashSet<BaseVarArc> queue = new LinkedHashSet<>();
        for (String fName : getFuture()) {
            BaseVariable f = getProblem().getVariable(fName);
            LinkedHashSet<NaryConstraint> common = f.getCommonConstraints(var);
            if (common == null) {
                continue;
            }
            queue.add(new BaseVarArc(f, var, common));
        }
        return queue;
    }

}

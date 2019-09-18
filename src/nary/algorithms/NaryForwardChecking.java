package nary.algorithms;

import nary.ECSProblem;
import nary.constraints.NaryConstraint;
import nary.types.BaseVariable;

import java.util.LinkedHashSet;

/**
 * An implementation of the Forward Checking constraint solving algorithm
 * specifically for {@link ECSProblem}s.
 *
 * @author 150009974
 * @version 1.2
 */
public class NaryForwardChecking extends NarySolving {

    /**
     * Creates a {@link NaryForwardChecking} instance
     * to solve the given {@link ECSProblem}.
     *
     * @param problem the {@link ECSProblem} to solve
     */
    public NaryForwardChecking(ECSProblem problem) {
        super(problem);
    }

    @Override
    protected boolean revise(BaseVariable var, LinkedHashSet<BaseVarPrune> prunes) {
        for (String fName : getFuture()) {
            BaseVariable f = getProblem().getVariable(fName);
            LinkedHashSet<NaryConstraint> common = f.getCommonConstraints(var);
            if (common == null) {
                continue;
            }
            BaseVarArc a = new BaseVarArc(f, var, common);
            incrementRevisionCounter();
            BaseVarPrune entry = a.prune(getPast());
            prunes.add(entry);
            if (!f.isConsistent()) {
                return false;
            }
        }
        return true;
    }

}

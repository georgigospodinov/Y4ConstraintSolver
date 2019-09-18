package binary.types;

import binary.BinaryCSP;

import java.util.HashSet;

/**
 * Represents a variable in a constraint problem,
 * the domain of which is iterated in ascending order.
 *
 * @author 150009974
 * @version 2.0
 */
public class AscendingVariable extends Variable {

    /**
     * Creates an {@link AscendingVariable} with the specified domain.
     *
     * @param index      the index of this {@link AscendingVariable}
     *                   in the {@link BinaryCSP}
     * @param lowerBound the lower bound of the domain
     * @param upperBound the upper bound of the domain
     */
    public AscendingVariable(int index, int lowerBound, int upperBound) {
        super(index, new HashSet<>());
        for (int i = lowerBound; i <= upperBound; i++) {
            addToDomain(i);
        }
    }

    @Override
    public int getNextVal() {
        /*
        The elements in a HashSet are iterated in ascending hash code order.
        An integer's hash code is itself.
        So integers in a HashSet are iterated in ascending order.
        */
        return domain.iterator().next();
    }

}

package binary.algorithms;

import binary.types.Variable;

import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Represents a Solution to a {@link binary.BinaryCSP}.
 * Maps a variable id to a value.
 * {@link Variable} values change over time.
 * This class supports persistence over time.
 *
 * @author 150009974
 * @version 2.0
 */
public class Solution extends LinkedHashMap<Integer, Integer> {

    /**
     * Creates a mapping between the {@link Variable} id and its value.
     *
     * @param c the collection of {@link Variable}s
     */
    public Solution(Collection<? extends Variable> c) {
        for (Variable var : c) {
            put(var.getId(), var.getValue());
        }
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        forEach((varId, val) -> {
            String line = "Var " + varId + " = " + val + "\n";
            buffer.append(line);
        });
        return buffer.toString();
    }

}

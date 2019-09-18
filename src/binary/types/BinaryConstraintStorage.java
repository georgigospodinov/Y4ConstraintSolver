package binary.types;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Represents a storage for {@link BinaryConstraint}s.
 * Provides methods to store and retrieve {@link BinaryConstraint}s
 * in constant time.
 * Allows indexing with two {@link Variable} IDs.
 *
 * @author 150009974
 * @version 1.01
 */
public class BinaryConstraintStorage {

    /**
     * Maps an {@link Integer} to
     * a map of {@link Integer}s to {@link BinaryConstraint}s.
     * This way a {@link BinaryConstraint} can be uniquely identified
     * by the IDs of its two variables.
     */
    private LinkedHashMap<Integer, LinkedHashMap<Integer, BinaryConstraint>> storage = new LinkedHashMap<>();

    /**
     * Associates the given {@link Variable} ids
     * with the given {@link BinaryConstraint}.
     *
     * @param id1        the first {@link Variable} id
     * @param id2        the second {@link Variable} id
     * @param constraint the {@link BinaryConstraint}
     */
    public void map(Integer id1, Integer id2, BinaryConstraint constraint) {
        if (!storage.containsKey(id1)) {
            storage.put(id1, new LinkedHashMap<>());
        }
        storage.get(id1).put(id2, constraint);
    }

    /**
     * @param id1 the id of the first {@link Variable}
     * @param id2 the id of the second {@link Variable}
     *
     * @return the {@link BinaryConstraint} associated with the given {@link Variable} ids
     */
    public BinaryConstraint get(Integer id1, Integer id2) {
        if (!storage.containsKey(id1)) {
            return null;
        } else {
            return storage.get(id1).get(id2);
        }
    }

    /** @return a list of all stored {@link BinaryConstraint}s */
    public LinkedList<BinaryConstraint> getConstraints() {
        LinkedList<BinaryConstraint> constraints = new LinkedList<>();
        storage.values().forEach(map -> constraints.addAll(map.values()));
        return constraints;
    }

    @Override
    public String toString() {
        return "BinaryConstraintStorage{" + storage + "}";
    }

}

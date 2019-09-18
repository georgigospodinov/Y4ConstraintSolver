package nary.types;

import nary.Utilities;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Represents a matrix in the high-level constraint satisfaction problem.
 *
 * @author 150009974
 * @version 2.2
 */
public class Matrix {

    /** The separator between matrix indexes and matrix values. */
    private static final String INDEXES_VALUES_SEPARATOR = "of values";

    /**
     * Parses the given {@link String} as a {@link Matrix} declaration.
     * Then creates and returns all {@link BaseVariable}s that
     * comprise that {@link Matrix}.
     * Returns null if the given {@link String} is not a valid
     * {@link Matrix} declaration.
     *
     * @return the created {@link BaseVariable}s or null if invalid declaration
     */
    public static Collection<BaseVariable> parse(String declaration) {
        String[] nameParam = Utilities.splitNameAndValues(declaration);

        // Name starts after 'matrix'
        String name = nameParam[0].substring(Utilities.MATRIX.length()).trim();
        String[] indexesValues = nameParam[1].split(INDEXES_VALUES_SEPARATOR);
        if (indexesValues.length != 2) {
            String msg = "Exactly one \"" + INDEXES_VALUES_SEPARATOR
                    + "\" must separate the indexes from the values.";
            throw new IllegalArgumentException(msg);
        }
        String indexes = indexesValues[0].trim();
        if (!indexes.startsWith("[") || !indexes.endsWith("]")) {
            String msg = "Matrix dimensions must be in square brackets.";
            throw new IllegalArgumentException(msg);
        }
        String combinedDims = indexes.substring(1, indexes.length() - 1);
        String[] dims = combinedDims.split(Utilities.DOMAIN_SEPARATOR);
        Matrix matrix = new Matrix(name, dims.length);

        int[][] limits = parseLimits(dims);
        int[] minMax = Utilities.parseMinMax(indexesValues[1]);
        matrix.setMin(minMax[Utilities.MIN_INDEX]);
        matrix.setMax(minMax[Utilities.MAX_INDEX]);
        return matrix.createBaseVariables(limits);
    }

    /**
     * Parses the limits from the raw {@link String}s.
     *
     * @param dims the raw dimensions (limits) to parse
     *
     * @return the parsed limits in a "dimension x 2" matrix
     * where dimension is the length of dims
     */
    private static int[][] parseLimits(String[] dims) {
        int[][] limits = new int[dims.length][2];
        for (int i = 0; i < dims.length; i++) {
            int[] minMax = Utilities.parseMinMax(dims[i]);
            limits[i][0] = minMax[Utilities.MIN_INDEX];
            limits[i][1] = minMax[Utilities.MAX_INDEX];
        }
        return limits;
    }

    /** The name of this {@link Matrix}. */
    private String name;

    /** The number of dimensions this {@link Matrix} has */
    private int dimensions;

    /** Maps arrays of indexes to a {@link BaseVariable}. */
    private LinkedHashMap<Integer[], BaseVariable> map = new LinkedHashMap<>();

    /** The minimum value in this matrix. */
    private int min;

    /** The maximum value in this matrix. */
    private int max;

    /**
     * Constructs a {@link Matrix} with the given name and dimensions.
     *
     * @param matrixName   the name of the matrix
     * @param noDimensions the number of dimensions
     */
    private Matrix(String matrixName, int noDimensions) {
        name = matrixName;
        dimensions = noDimensions;
    }

    /** @param min the minimum value that can appear in this {@link Matrix} */
    private void setMin(int min) {
        this.min = min;
    }

    /** @param max the maximum value that can appear in this {@link Matrix} */
    private void setMax(int max) {
        this.max = max;
    }

    /**
     * Creates {@link BaseVariable}s to represent this {@link Matrix}.
     * These
     *
     * @param limits a "dimensions x 2" matrix
     *               giving the first and last index in each dimension
     *
     * @return the created {@link BaseVariable}s
     */
    private Collection<BaseVariable> createBaseVariables(int[][] limits) {
        recurseForEachValue(limits, new LinkedList<>());
        return map.values();
    }

    /**
     * Recursively generates all combinations of keys defined by the limits.
     *
     * @param limits the index limits defined by the matrix
     * @param keys   the current list of keys
     *
     * @see Matrix#checkBottom(int[][], LinkedList)
     */
    private void recurseForEachValue(int[][] limits, LinkedList<Integer> keys) {
        int index = keys.size();
        int startIndex = limits[index][0];
        int endIndex = limits[index][1];
        for (int i = startIndex; i <= endIndex; i++) {
            keys.addLast(i);
            checkBottom(limits, keys);
            keys.removeLast();
        }
    }

    /**
     * Checks if the recursion has hit the bottom.
     * That is, there are enough keys to index the matrix.
     * If so, instantiates an appropriate {@link BaseVariable}.
     * Otherwise, continues the recursion.
     *
     * @param limits the index limits defined by the matrix
     * @param keys   the current list of keys
     *
     * @see Matrix#recurseForEachValue(int[][], LinkedList)
     */
    private void checkBottom(int[][] limits, LinkedList<Integer> keys) {
        if (keys.size() == dimensions) {
            instantiateBaseVar(keys);
            return;
        }
        recurseForEachValue(limits, keys);
    }

    /**
     * Instantiates a new {@link BaseVariable} using the given keys
     * to set its name and maps the keys to the {@link BaseVariable}.
     *
     * @param keys the keys to index this {@link Matrix}
     */
    private void instantiateBaseVar(LinkedList<Integer> keys) {
        StringBuilder varName = new StringBuilder(name);
        for (Integer k : keys) {
            varName.append("_");
            varName.append(k);
            varName.append("_");
        }
        Integer[] key = keys.toArray(new Integer[0]);
        map.put(key, new BaseVariable(varName.toString(), min, max));
    }

}

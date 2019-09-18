package binary;

import binary.types.BinaryConstraint;
import binary.types.BinaryConstraintStorage;
import binary.types.BinaryTuple;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.LinkedHashSet;

/**
 * A reader tailored for binary extensional CSPs.
 * It is created from a FileReader and a StreamTokenizer.
 *
 * @version 2.2
 */
public final class BinaryCSPReader {

    /** The {@link StreamTokenizer} used to read the csp file. */
    private static StreamTokenizer tokenizer;

    /**
     * A main method for simple testing.
     * Parses the csp file given as command line argument
     * and prints the parsed {@link BinaryCSP} to standard output.
     *
     * @param args a single argument specifying csp file name
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java binary.BinaryCSPReader <file.csp>");
            return;
        }
        try {
            BinaryCSP csp = readBinaryCSP(args[0]);
            System.out.println(csp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a {@link BinaryCSP} from the specified filename.
     * Expect the following format.
     * File format:
     * <no. vars>
     * NB vars indexed from 0
     * We assume that the domain of all vars is specified in terms of bounds
     * <lb>, <ub> (one per var)
     * Then the list of constraints
     * c(<varno>, <varno>)
     * binary tuples
     * <domain val>, <domain val>
     *
     * @param filename the name of the file to read
     *
     * @return the parsed {@link BinaryCSP}
     *
     * @throws IOException if an I/O error occurs
     */
    public static BinaryCSP readBinaryCSP(String filename) throws IOException {
        FileReader fileReader = instantiateTokenizer(filename);
        // number of variables
        tokenizer.nextToken();
        int n = (int) tokenizer.nval;
        int[][] domainBounds = readDomainBounds(n);
        BinaryConstraintStorage constraints = readBinaryConstraints();
        BinaryCSP csp = new BinaryCSP(domainBounds, constraints);
        fileReader.close();
        return csp;
    }

    /**
     * Instantiates the {@link BinaryCSPReader#tokenizer} to read
     * the specified file.
     *
     * @param filename the name of the file to read
     *
     * @return the instantiated {@link FileReader}
     * that has to be closed after parsing
     *
     * @throws FileNotFoundException if the specified file
     *                               cannot be opened for reading
     */
    private static FileReader instantiateTokenizer(String filename)
            throws FileNotFoundException {
        FileReader fileReader = new FileReader(filename);
        tokenizer = new StreamTokenizer(fileReader);
        // Parentheses are not special.
        tokenizer.ordinaryChar('(');
        tokenizer.ordinaryChar(')');
        return fileReader;
    }

    /**
     * Reads and returns the domain bounds for the variables of the CSP.
     *
     * @param n the amount of variables in the csp
     *
     * @return the parsed domain bounds
     *
     * @throws IOException if an I/O error occurs
     * @see BinaryCSPReader#readBinaryCSP(String)
     */
    private static int[][] readDomainBounds(int n) throws IOException {
        int[][] bounds = new int[n][2];
        for (int i = 0; i < n; i++) {
            tokenizer.nextToken();  // i-th upper bound
            bounds[i][0] = (int) tokenizer.nval;
            tokenizer.nextToken();  // ',' comma separator
            tokenizer.nextToken();  // i-th lower bound
            bounds[i][1] = (int) tokenizer.nval;
        }
        return bounds;
    }

    /**
     * Reads and returns the {@link BinaryConstraint}s of the CSP.
     *
     * @return the parsed {@link BinaryConstraint}s
     *
     * @throws IOException if an I/O error occurs
     * @see BinaryCSPReader#readBinaryCSP(String)
     */
    private static BinaryConstraintStorage readBinaryConstraints()
            throws IOException {
        BinaryConstraintStorage constraints = new BinaryConstraintStorage();
        tokenizer.nextToken();  //'c' or EOF
        while (tokenizer.ttype != StreamTokenizer.TT_EOF) {
            // scope
            tokenizer.nextToken();  // '(' opening parentheses
            tokenizer.nextToken();  // var1 index
            int var1 = (int) tokenizer.nval;
            tokenizer.nextToken();  // ',' comma separator
            tokenizer.nextToken();  // var2 index
            int var2 = (int) tokenizer.nval;
            tokenizer.nextToken();  // ')' closing parentheses

            LinkedHashSet<BinaryTuple> tuples = readBinaryTuples();
            BinaryConstraint c = new BinaryConstraint(var1, var2, tuples);
            constraints.map(var1, var2, c);
        }

        return constraints;
    }

    /**
     * Reads and returns the {@link BinaryTuple}s of a {@link BinaryConstraint}.
     *
     * @return the parsed {@link BinaryTuple}s
     *
     * @throws IOException if an I/O error occurs
     * @see BinaryCSPReader#readBinaryConstraints()
     * @see BinaryCSPReader#readBinaryCSP(String)
     */
    private static LinkedHashSet<BinaryTuple> readBinaryTuples() throws IOException {
        LinkedHashSet<BinaryTuple> tuples = new LinkedHashSet<>();
        tokenizer.nextToken();  // 1st allowed val of 1st tuple
        while (constraintIsNotOver()) {
            int val1 = (int) tokenizer.nval;  // 1st val
            tokenizer.nextToken();  // ',' comma separator
            tokenizer.nextToken();  // 2nd val
            int val2 = (int) tokenizer.nval;
            tuples.add(new BinaryTuple(val1, val2));

            // either 1st allowed val of next tuple or c/EOF
            tokenizer.nextToken();
        }
        return tuples;
    }

    /**
     * Determines if the currently parsed constraint is over.
     * In other words, the token is
     * not end of file
     * and not start of a new constraint.
     *
     * @return true iff the current constraint is not yet over
     *
     * @see BinaryCSPReader#readBinaryTuples()
     */
    private static boolean constraintIsNotOver() {
        // NB: tokenizer.sval can be null
        return !"c".equals(tokenizer.sval)
                && tokenizer.ttype != StreamTokenizer.TT_EOF;
    }

    /** Hides the default constructor for this utility class. */
    private BinaryCSPReader() {
    }

}

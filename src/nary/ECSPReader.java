package nary;

import nary.constraints.AllDiff;
import nary.constraints.Exists;
import nary.constraints.ForAll;
import nary.constraints.NaryConstraint;
import nary.constraints.UnaryConstraint;
import nary.types.BaseVariable;
import nary.types.Constant;
import nary.types.Matrix;
import org.mariuszgromada.math.mxparser.Expression;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * A reader tailored for the custom formatted ECSP files.
 *
 * @author 150009974
 * @version 2.5
 */
public final class ECSPReader {

    /**
     * Parses the specified file and returns the created {@link ECSProblem}.
     *
     * @param filename the name of the file to parse
     *
     * @return the created {@link ECSProblem} instance
     */
    public static ECSProblem parse(String filename) {
        return new ECSPReader(filename).parseProblem();
    }

    /** The {@link Scanner} used to read the ecsp file. */
    private Scanner scanner;

    /** The number of the current line. */
    private int currentLineNumber;

    /*
    NOTE: mapping names to Constants and BaseVariables allows
    for checking of duplicates.
    */

    /** The declared {@link Constant}s in the ecsp file. */
    private LinkedHashMap<String, Constant> constants = new LinkedHashMap<>();

    /** The declared {@link BaseVariable}s in the ecsp file. */
    private LinkedHashMap<String, BaseVariable> vars = new LinkedHashMap<>();

    /** The declared {@link UnaryConstraint}s in the ecsp file. */
    private LinkedHashSet<UnaryConstraint> unaryConstraints = new LinkedHashSet<>();

    /** The declared {@link NaryConstraint}s in the ecsp file. */
    private ArrayList<NaryConstraint> naryConstraints = new ArrayList<>();

    /** The {@link ECSProblem} from the ecsp file. */
    private ECSProblem problem = new ECSProblem();

    /**
     * Creates a {@link ECSPReader} for the specified file.
     *
     * @param filename the name of the file to parse
     */
    private ECSPReader(String filename) {
        try {
            scanner = new Scanner(new File(filename), "utf-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses and returns the {@link ECSProblem}.
     *
     * @return the parsed {@link ECSProblem}
     */
    public ECSProblem parseProblem() {
        currentLineNumber = 0;
        while (scanner.hasNextLine()) {
            try {
                String line = readNextLine();
                if (!parseLine(line)) {
                    failedToRecognize(line);
                }
            } catch (IllegalArgumentException iae) {
                String msg =
                        "Line " + currentLineNumber + ": " + iae.getMessage();
                IllegalArgumentException e = new IllegalArgumentException(msg);
                e.addSuppressed(iae);
                throw e;
            } catch (NoSuchElementException nse) {
                // end of input
            }
        }
        return constructProblem();
    }

    /**
     * Reads the next line from the {@link ECSPReader#scanner},
     * advances the {@link ECSPReader#currentLineNumber} and
     * returns the read line.
     * Comments and empty lines are skipped.
     * Constants are replaced.
     *
     * @return the line read
     */
    private String readNextLine() {
        String line = "";
        while (line.isEmpty() || line.startsWith("//")) {
            line = scanner.nextLine().trim();
            currentLineNumber++;
        }
        return replaceConstants(line);
    }

    /**
     * Replaces occurrences of constants in the given line.
     * Returns the line with line constants.
     *
     * @param line the line to search constants in
     *
     * @return the line with the line constants
     */
    private String replaceConstants(String line) {
        for (Constant constant : constants.values()) {
            String name = Pattern.quote(constant.getName());
            int value = constant.getValue();

            // Occurrence at start
            line = line.replaceAll("^" + name + " ", value + " ");
            // Occurrence in the middle
            line = line.replaceAll(" " + name + " ", " " + value + " ");
            // Occurrence at end
            line = line.replaceAll(" " + name + "$", " " + value);

            // Occurrence as matrix declaration
            line = line.replaceAll("\\[" + name + " ", "[" + value + " ");
            line = line.replaceAll(" " + name + "]", " " + value + "]");
            line = line.replaceAll(" " + name + ",", " " + value + ",");
            // Occurrence as matrix index
            line = line.replaceAll("_" + name + "_", "_" + value + "_");
            line = line.replaceAll("\\[" + name + "]", "_" + value + "_");
        }
        return line;
    }

    /**
     * Attempts to recognize and parse the given line.
     * If the line starts with "//" or is empty, it is skipped.
     * If it's a constant, variable, matrix or constraint declaration,
     * it is parsed.
     * Returns true if the line was successfully recognized and parsed.
     * Returns false if the line was not recognized.
     *
     * @param line the line itself
     *
     * @return true iff the line was recognized and parsed
     *
     * @throws IllegalArgumentException if the line is an invalid declaration
     */
    private boolean parseLine(String line) {
        return parseVariableDeclaration(line) || parseConstraint(line);
    }

    /**
     * Attempts to parse the given line as a keyword declaration.
     *
     * @param line the line to parse
     *
     * @return true iff the line was recognized and parsed
     */
    private boolean parseVariableDeclaration(String line) {
        if (line.startsWith(Utilities.CONSTANT)) {
            parseConstant(line);
        } else if (line.startsWith(Utilities.VARIABLE)) {
            parseVariable(line);
        } else if (line.startsWith(Utilities.MATRIX)) {
            parseMatrix(line);
        } else {
            return false;
        }
        return true;
    }

    /**
     * Attempts to parse the given line as a {@link Constant} declaration.
     *
     * @param line the declaration to parse
     */
    private void parseConstant(String line) {
        Constant c = Constant.parse(line);
        String name = c.getName();
        if (constants.containsKey(name) || vars.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate declaration.");
        }
        constants.put(name, c);
    }

    /**
     * Attempts to parse the given line as a variable declaration.
     *
     * @param line the declaration to parse
     */
    private void parseVariable(String line) {
        BaseVariable var = BaseVariable.parse(line);
        String name = var.getName();
        if (vars.containsKey(name) || constants.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate declaration.");
        } else {
            vars.put(name, var);
        }
    }

    /**
     * Attempts to parse the given line as a matrix declaration.
     *
     * @param line the declaration to parse
     */
    private void parseMatrix(String line) {
        Collection<BaseVariable> variables = Matrix.parse(line);
        for (BaseVariable var : variables) {
            String name = var.getName();
            if (vars.containsKey(name) || constants.containsKey(name)) {
                throw new IllegalArgumentException("Duplicate declaration.");
            } else {
                vars.put(name, var);
            }
        }
    }

    /**
     * Attempts to parse the given line as an All-Diff constraint declaration.
     *
     * @param line the declaration to parse
     */
    private void parseAllDiff(String line) {
        LinkedList<NaryConstraint> constraints = new AllDiff(vars).parse(line);
        for (NaryConstraint constraint : constraints) {
            constraint.setProblem(problem);
            naryConstraints.add(constraint);
        }
    }

    /**
     * Attempts to parse the given lines as a For-All constraint declaration.
     *
     * @param forAllLine the line declaring a For-All
     * @param nextLine   the next line
     */
    private void parseForAll(String forAllLine, String nextLine) {
        ArrayList<String> declarations = ForAll.parse(forAllLine, nextLine);
        for (String declaration : declarations) {
            parseConstraint(declaration);
        }
    }

    /**
     * Attempts to parse the given lines as an Exists constraint declaration.
     *
     * @param existsLine the line declaring an Exists
     * @param nextLine   the next line
     */
    private void parseExists(String existsLine, String nextLine) {
        String disjunction = Exists.parse(existsLine, nextLine);
        parseConstraint(disjunction);
    }

    /**
     * Attempts to parse the given line as a constraint declaration.
     *
     * @param line the line to parse
     *
     * @return true iff the line was successfully parsed
     */
    private boolean parseConstraint(String line) {
        if (line.startsWith(Utilities.ALL_DIFF)) {
            parseAllDiff(line);
        } else if (line.startsWith(Utilities.FOR_ALL)) {
            parseForAll(line, readNextLine());
        } else if (line.startsWith(Utilities.EXISTS)) {
            parseExists(line, readNextLine());
        } else {
            line = replaceMatrixAccesses(line);
            return parseUnary(line) || parseNary(line);
        }
        return true;
    }

    /**
     * Replaces matrix accesses with variables names in the given line.
     * Matrix accesses via expressions are evaluated before replacement.
     * Returns the line with the replaced matrix accesses.
     *
     * @param line the line to search matrix accesses in
     *
     * @return the line with the replaced matrix accesses
     */
    private String replaceMatrixAccesses(String line) {
        int start = line.indexOf("[");
        int end = line.indexOf("]");
        while (start != -1 && end != -1 && start < end) {
            String access = line.substring(start + 1, end);
            Expression exp = new Expression(access.trim());
            int index = (int) exp.calculate();
            // This escapes all regex special characters.
            String quoted = Pattern.quote(access);
            line = line.replaceAll("\\[" + quoted + "]", "_" + index + "_");
            start = line.indexOf("[");
            end = line.indexOf("]");
        }
        return line;
    }

    /**
     * Attempts to parse the given line as a unary constraint declaration.
     * Returns true if a {@link UnaryConstraint} was parsed and stored.
     * Otherwise returns false.
     *
     * @param line the declaration to parse
     *
     * @return true iff a {@link UnaryConstraint} was parsed and stored
     */
    private boolean parseUnary(String line) {
        UnaryConstraint unary = UnaryConstraint.parse(line);
        if (unary == null) {
            return false;
        }
        // some validation might be useful
        unaryConstraints.add(unary);
        return true;
    }

    /**
     * Attempts to parse the given line as a naray constraint declaration.
     * Returns true if a {@link NaryConstraint} was parsed and stored.
     * Otherwise returns false.
     *
     * @param line the declaration to parse
     *
     * @return true iff a {@link NaryConstraint} was parsed and stored
     */
    private boolean parseNary(String line) {
        NaryConstraint nary = NaryConstraint.parse(line);
        if (nary == null) {
            return false;
        }
        // some validation might be useful
        nary.setProblem(problem);
        naryConstraints.add(nary);
        return true;
    }

    /**
     * Throws an {@link IllegalArgumentException} with a message
     * saying that the line was not recognized.
     *
     * @param line the declaration to parse
     */
    private void failedToRecognize(String line) {
        String error = "Could not recognize line: " + line;
        throw new IllegalArgumentException(error);
    }

    /**
     * Sets the parameters of the {@link ECSPReader#problem} and returns it.
     *
     * @return the constructed {@link ECSProblem}
     */
    private ECSProblem constructProblem() {
        problem.setVariables(vars);
        problem.setUnary(unaryConstraints);
        problem.setNary(naryConstraints);
        return problem;
    }

}

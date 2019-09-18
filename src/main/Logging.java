package main;

/**
 * This class provides methods to determine
 * what info should be logged to standard output.
 *
 * @author 150009974
 * @version 1.2
 */
public final class Logging {

    // Note that these constant should be powers of two.

    /** The constant for logging branches. */
    private static final int BRANCHES = 1;

    /** The constant for logging domain wipeouts. */
    private static final int DOMAIN_WIPEOUT = 2;

    /** The constant for logging the state of past and future variables. */
    private static final int VAR_STATE = 4;

    /** The constant for logging arc revision. */
    private static final int ARC_REVISION = 8;

    /** The constant for logging the arc revision's constraint. */
    private static final int ARC_REVISION_CONSTRAINT = 16;

    /** The logging configuration. */
    private static int config = 0;

    /** @return true iff branches should be logged */
    public static boolean logBranches() {
        return (config & BRANCHES) > 0;
    }

    /** @return true iff domain wipeouts should be logged */
    public static boolean logWipeouts() {
        return (config & DOMAIN_WIPEOUT) > 0;
    }

    /** @return true iff variable states should be logged */
    public static boolean logVarStates() {
        return (config & VAR_STATE) > 0;
    }

    /** @return true iff arc revisions should be logged */
    public static boolean logArcRevision() {
        return (config & ARC_REVISION) > 0;
    }

    /** @return true iff arc revisions' constraints should be logged */
    public static boolean logArcRevisionConstraints() {
        return (config & ARC_REVISION_CONSTRAINT) > 0;
    }

    /** @param log the logging configuration to set */
    // package private method. Only main.ArgumentParser should set this.
    static void setLogConfig(int log) {
        config = log;
    }

}

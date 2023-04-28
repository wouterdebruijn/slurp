package nl.wouterdebruijn.slurp.exceptions;

public class MissingSessionException extends Exception {
    public MissingSessionException() {
        super("Session is missing in local storage.");
    }
}

package nl.wouterdebruijn.slurp.helper.slurp.exceptions;

public class MissingSessionException extends Exception {
    public MissingSessionException() {
        super("Session is missing in local storage.");
    }
}

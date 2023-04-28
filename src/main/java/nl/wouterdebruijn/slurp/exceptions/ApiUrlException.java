package nl.wouterdebruijn.slurp.exceptions;

public class ApiUrlException extends ApiException {
    public ApiUrlException() {
        super(null, "The API URL is invalid.");
    }
}

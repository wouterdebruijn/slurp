package nl.wouterdebruijn.slurp.helpers.slurp.exceptions;

import java.net.http.HttpRequest;

public class ApiUrlException extends ApiException {
    public ApiUrlException() {
        super(null, "The API URL is invalid.");
    }
}

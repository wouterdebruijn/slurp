package nl.wouterdebruijn.slurp.helpers.slurp.exceptions;

import java.net.http.HttpRequest;

public class ApiException extends Exception {
    HttpRequest request;
    public ApiException(HttpRequest request) {
        super("An Slurp API error occurred.");
    }

    public ApiException(HttpRequest request, String message) {
        super(message);
        this.request = request;
    }
}

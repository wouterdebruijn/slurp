package nl.wouterdebruijn.slurp.helpers.slurp.exceptions;

import java.net.http.HttpRequest;

public class CreateSessionException extends ApiException {

    public CreateSessionException(HttpRequest request) {
        super(request);
    }
}

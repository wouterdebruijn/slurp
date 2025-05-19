package nl.wouterdebruijn.slurp.exceptions;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;

import nl.wouterdebruijn.slurp.Slurp;

public class ApiResponseException extends Exception {
    HttpRequest request;
    HttpResponse<String> response;

    public ApiResponseException(HttpRequest request, HttpResponse<String> response) {
        super("A Slurp API error occurred.");
        this.request = request;
        this.response = response;

        Slurp.logger.log(Level.SEVERE,
                String.format("An error occurred while sending a request to the Slurp API. Request: %s, Response: %s",
                        request, response.body()));
    }
}

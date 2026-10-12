package co.meedoo.sdk;

public class MeeDooException extends Exception {
    private final int statusCode;

    public MeeDooException(String message) {
        super(message);
        this.statusCode = 0;
    }

    public MeeDooException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() { return statusCode; }
}

class AuthenticationException extends MeeDooException {
    public AuthenticationException(String message) { super(message, 401); }
}

class RateLimitException extends MeeDooException {
    public RateLimitException(String message) { super(message, 429); }
}

class InsufficientCreditsException extends MeeDooException {
    public InsufficientCreditsException(String message) { super(message, 400); }
}

class ModelNotFoundException extends MeeDooException {
    public ModelNotFoundException(String message) { super(message, 404); }
}

class APIException extends MeeDooException {
    public APIException(String message) { super(message); }
    public APIException(String message, int code) { super(message, code); }
}
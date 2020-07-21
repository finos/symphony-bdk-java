package exceptions;

public class ForbiddenException extends SymClientException {

    public ForbiddenException(String message) {
        super(message);
    }
}
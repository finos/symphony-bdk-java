package exceptions;

public class UnauthorizedException extends SymClientException {

    public UnauthorizedException(String message) {
        super(message);
    }
}

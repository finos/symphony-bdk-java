package exceptions;

public class ServerErrorException extends SymClientException {

    public ServerErrorException(String message) {
        super(message);
    }
}

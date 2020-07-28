package exceptions;

public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Exception rootException) {
        super(rootException);
    }

    @Deprecated
    public Exception getRootException() {
        return (Exception) this.getCause();
    }

    @Deprecated
    public boolean hasRootException() {
        return this.getRootException() != null;
    }
}

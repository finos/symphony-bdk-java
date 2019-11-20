package exceptions;

public class AuthenticationException extends Exception {
    private Exception rootException;

    public AuthenticationException(Exception rootException) {
        super(rootException);
        this.rootException = rootException;
    }

    public Exception getRootException() {
        return rootException;
    }

    public void setRootException(Exception rootException) {
        this.rootException = rootException;
    }
}

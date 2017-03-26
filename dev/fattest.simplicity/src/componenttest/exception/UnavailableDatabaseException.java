package componenttest.exception;

public class UnavailableDatabaseException extends Exception {

    public UnavailableDatabaseException() {
        super();
    }

    public UnavailableDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnavailableDatabaseException(String message) {
        super(message);

    }

    public UnavailableDatabaseException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 1L;
}

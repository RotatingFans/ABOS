public class addressException extends RuntimeException {
    public addressException() { super(); }

    public addressException(String message) { super(message); }

    public addressException(String message, Throwable cause) { super(message, cause); }

    public addressException(Throwable cause) { super(cause); }
}

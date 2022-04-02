package customexception;

public class InvalidObjectFieldNameException extends RuntimeException {
    public InvalidObjectFieldNameException(final String msg) {
        super(msg);
    }
}
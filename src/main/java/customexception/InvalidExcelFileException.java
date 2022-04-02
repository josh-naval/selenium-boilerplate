package customexception;

public class InvalidExcelFileException extends RuntimeException {
    public InvalidExcelFileException(final String msg) {
        super(msg);
    }
}
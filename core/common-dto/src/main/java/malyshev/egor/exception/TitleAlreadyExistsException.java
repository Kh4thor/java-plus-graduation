package malyshev.egor.exception;

public class TitleAlreadyExistsException extends RuntimeException {
    public TitleAlreadyExistsException(String title) {
        super("Compilation with title '" + title + "' already exists");
    }
}
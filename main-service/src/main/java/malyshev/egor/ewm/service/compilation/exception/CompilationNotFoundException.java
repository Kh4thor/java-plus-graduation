package malyshev.egor.ewm.service.compilation.exception;

public class CompilationNotFoundException extends RuntimeException {
    public CompilationNotFoundException(Long id) {
        super("Compilation with id=" + id + " was not found");
    }
}

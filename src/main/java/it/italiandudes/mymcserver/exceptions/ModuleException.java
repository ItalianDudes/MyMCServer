package it.italiandudes.mymcserver.exceptions;

@SuppressWarnings("unused")
public class ModuleException extends Exception {
    public ModuleException(String message) {
        super(message);
    }
    public ModuleException(String message, Throwable cause) {
        super(message, cause);
    }
}

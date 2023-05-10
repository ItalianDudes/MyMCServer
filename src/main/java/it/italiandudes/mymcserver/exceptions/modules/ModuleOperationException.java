package it.italiandudes.mymcserver.exceptions.modules;

import it.italiandudes.mymcserver.exceptions.ModuleException;

@SuppressWarnings("unused")
public class ModuleOperationException extends ModuleException {
    public ModuleOperationException(String message) {
        super(message);
    }
    public ModuleOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

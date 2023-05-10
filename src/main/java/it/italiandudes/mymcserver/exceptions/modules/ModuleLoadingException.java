package it.italiandudes.mymcserver.exceptions.modules;

import it.italiandudes.mymcserver.exceptions.ModuleException;

@SuppressWarnings("unused")
public class ModuleLoadingException extends ModuleException {
    public ModuleLoadingException(String message) {
        super(message);
    }
    public ModuleLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}

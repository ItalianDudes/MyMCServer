package it.italiandudes.mymcserver.exceptions.modules;

import it.italiandudes.mymcserver.exceptions.ModuleException;

@SuppressWarnings("unused")
public class ModuleNotLoadedException extends ModuleException {
    public ModuleNotLoadedException(String message) {
        super(message);
    }
    public ModuleNotLoadedException(String message, Throwable cause) {
        super(message, cause);
    }
}

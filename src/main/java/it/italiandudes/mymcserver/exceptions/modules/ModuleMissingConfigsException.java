package it.italiandudes.mymcserver.exceptions.modules;

import it.italiandudes.mymcserver.exceptions.ModuleException;

@SuppressWarnings("unused")
public class ModuleMissingConfigsException extends ModuleException {
    public ModuleMissingConfigsException(String message) {
        super(message);
    }
    public ModuleMissingConfigsException(String message, Throwable cause) {
        super(message, cause);
    }
}

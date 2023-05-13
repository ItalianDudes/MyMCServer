package it.italiandudes.mymcserver.exceptions.modules;

import it.italiandudes.mymcserver.exceptions.ModuleException;

@SuppressWarnings("unused")
public class ModuleMissingDependenciesException extends ModuleException {
    public ModuleMissingDependenciesException(String message) {
        super(message);
    }
    public ModuleMissingDependenciesException(String message, Throwable cause) {
        super(message, cause);
    }
}

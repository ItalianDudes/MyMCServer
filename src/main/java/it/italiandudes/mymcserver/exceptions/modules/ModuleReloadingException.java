package it.italiandudes.mymcserver.exceptions.modules;

import it.italiandudes.mymcserver.exceptions.ModuleException;

@SuppressWarnings("unused")
public final class ModuleReloadingException extends ModuleException {
    public ModuleReloadingException(String message) {
        super(message);
    }
    public ModuleReloadingException(String message, Throwable cause) {
        super(message, cause);
    }
}

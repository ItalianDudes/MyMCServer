package it.italiandudes.mymcserver.utils;

import org.bukkit.Bukkit;

import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class ServerLogger {

    // Default Constructor
    public ServerLogger() {
        throw new RuntimeException("Can't instantiate this class!");
    }

    // Logger Getter
    public static Logger getLogger() {
        return Bukkit.getLogger();
    }
}

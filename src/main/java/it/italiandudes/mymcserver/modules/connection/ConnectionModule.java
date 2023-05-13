package it.italiandudes.mymcserver.modules.connection;

import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.exceptions.modules.*;
import it.italiandudes.mymcserver.modules.DBConnectionModule;
import it.italiandudes.mymcserver.utils.ServerLogger;

import java.io.IOException;
import java.net.ServerSocket;

@SuppressWarnings("unused")
public final class ConnectionModule {

    // Attributes
    private static ServerListener serverListener = null;
    private static boolean isModuleLoaded = false;
    private static boolean areConnectionLoading = false;

    // Default Constructor
    public ConnectionModule() {
        throw new RuntimeException("Can't instantiate this class!");
    }

    // Module Checker
    public static boolean isModuleLoaded() {
        return isModuleLoaded && serverListener != null && serverListener.isAlive();
    }

    // Methods
    public synchronized static void load(final int port) throws ModuleException {
        load(port, false);
    }
    public synchronized static void load(final int port, final boolean disableLog) throws ModuleException {

        if (!DBConnectionModule.isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().warning("Connection Module Load: Canceled! (Reason: DBConnection module isn't loaded)");
            throw new ModuleMissingDependenciesException("Connection Module Load: Canceled! (Reason: DBConnection module isn't loaded)");
        }

        if (areConnectionLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Connection Module Load: Canceled! (Reason: Another thread is executing a connection loading command)");
            throw new ModuleLoadingException("Connection Module Load: Canceled! (Reason: Another thread is executing a connection loading command)");
        }
        if (isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Load: Failed! (Reason: the module has already been loaded)");
            throw new ModuleAlreadyLoadedException("Connection Module Load: Failed! (Reason: the module has already been loaded)");
        }

        areConnectionLoading = true;

        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(port);
        }catch (IllegalArgumentException illegalArgumentException) {
            areConnectionLoading = false;
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Load: Failed! (Reason: port out of bounds)");
            throw new ModuleLoadingException("Connection Module Load: Failed! (Reason: port out of bounds)", illegalArgumentException);
        }catch (IOException ioException) {
            areConnectionLoading = false;
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Load: Failed! (Reason: server opening error)");
            throw new ModuleLoadingException("Connection Module Load: Failed! (Reason: server opening error)", ioException);
        }

        serverListener = new ServerListener(serverSocket);
        serverListener.start();

        areConnectionLoading = false;
        isModuleLoaded = true;
        if (!disableLog) ServerLogger.getLogger().info("Connection Module Load: Successful!");
    }
    public synchronized static void unload() throws ModuleException {
        unload(false);
    }
    public synchronized static void unload(final boolean disableLog) throws ModuleException {

        if (areConnectionLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Connection Module Unload: Canceled! (Reason: Another thread is executing a connection loading command)");
            throw new ModuleLoadingException("Connection Module Unload: Canceled! (Reason: Another thread is executing a connection loading command)");
        }
        if (!isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Unload: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("Connection Module Unload: Failed! (Reason: the module isn't loaded)");
        }

        try {
            serverListener.closeListener();
        }catch (Exception ignored) {}

        serverListener = null;

        if(!disableLog) ServerLogger.getLogger().info("Connection Module Unload: Successful!");
    }
    public synchronized static void reload(final int port) throws ModuleException {
        reload(port, false);
    }
    public synchronized static void reload(final int port, final boolean disableLog) throws ModuleException {

        if (areConnectionLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Connection Module Reload: Canceled! (Reason: Another thread is executing a connection loading command)");
            throw new ModuleLoadingException("Connection Module Reload: Canceled! (Reason: Another thread is executing a connection loading command)");
        }
        if (!isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Reload: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("Connection Module Reload: Failed! (Reason: the module isn't loaded)");
        }

        try {
            unload(true);
        } catch (ModuleException e) {
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Reload: Failed! (Reason: the unload routine has failed)");
            throw new ModuleReloadingException("Connection Module Reload: Failed! (Reason: the unload routine has failed)", e);
        }

        try {
            load(port, true);
        } catch (ModuleException e) {
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Reload: Failed! (Reason: the load routine has failed)");
            throw new ModuleReloadingException("Connection Module Reload: Failed! (Reason: the load routine has failed)", e);
        }

        if (!disableLog) ServerLogger.getLogger().info("Connection Module Reload: Successful!");
    }

}

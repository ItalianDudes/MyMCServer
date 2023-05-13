package it.italiandudes.mymcserver.modules;

import it.italiandudes.idl.common.StringHandler;
import it.italiandudes.mymcserver.MyMCServer;
import it.italiandudes.mymcserver.commands.MyMCServerCommand;
import it.italiandudes.mymcserver.commands.mymcserver.modules.MMCSReloadCommand;
import it.italiandudes.mymcserver.commands.mymcserver.modules.MMCSUnloadCommand;
import it.italiandudes.mymcserver.commands.mymcserver.modules.MMCSLoadCommand;
import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleAlreadyLoadedException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleLoadingException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleNotLoadedException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleReloadingException;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.ServerLogger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings({"unused", "deprecation"})
public final class CommandsModule {

    // Attributes
    private static boolean isModuleLoaded = false;
    private static boolean areCommandsLoading = false;

    // Default Constructor
    public CommandsModule() {
        throw new RuntimeException("Can't instantiate this class!");
    }

    // Module Checker
    public static boolean isModuleLoaded() {
        return isModuleLoaded;
    }

    // Methods
    public synchronized static void load(@NotNull final JavaPlugin pluginInstance) throws ModuleException {
        load(pluginInstance, false);
    }
    public synchronized static void load(@NotNull final JavaPlugin pluginInstance, final boolean disableLog) throws ModuleException {

        if (areCommandsLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Commands Module Load: Canceled! (Reason: Another thread is executing a commands loading command)");
            throw new ModuleLoadingException("Commands Module Load: Canceled! (Reason: Another thread is executing a commands loading command)");
        }
        if (isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Commands Module Load: Failed! (Reason: the module has already been loaded)");
            throw new ModuleAlreadyLoadedException("Commands Module Load: Failed! (Reason: the module has already been loaded)");
        }

        areCommandsLoading = true;

        // List of commands here...
        try {
            registerCommand(MyMCServer.getPluginInstance(), MyMCServerCommand.COMMAND_NAME, new MyMCServerCommand());
            registerCommand(MyMCServer.getPluginInstance(), MMCSLoadCommand.COMMAND_NAME, new MMCSLoadCommand());
            registerCommand(MyMCServer.getPluginInstance(), MMCSUnloadCommand.COMMAND_NAME, new MMCSUnloadCommand());
            registerCommand(MyMCServer.getPluginInstance(), MMCSReloadCommand.COMMAND_NAME, new MMCSReloadCommand());
        } catch (Exception e) {
            areCommandsLoading = false;
            if (!disableLog) ServerLogger.getLogger().severe("Commands Module Load: Failed! (Reason: an error has occurred on module loading)");
            throw new ModuleLoadingException("Commands Module Load: Failed! (Reason: an error has occurred on module loading)", e);
        }

        areCommandsLoading = false;
        isModuleLoaded = true;
        if (!disableLog) ServerLogger.getLogger().info("Commands Module Load: Successful!");
    }
    private synchronized static void registerCommand(@NotNull final JavaPlugin PLUGIN_INSTANCE, @NotNull final String COMMAND_NAME, @NotNull final CommandExecutor COMMAND) {
        Objects.requireNonNull(PLUGIN_INSTANCE.getCommand(COMMAND_NAME)).setExecutor(COMMAND);
    }
    public synchronized static void unload() throws ModuleException {
        unload(false);
    }
    public synchronized static void unload(final boolean disableLog) throws ModuleException {

        if (areCommandsLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Commands Module Unload: Canceled! (Reason: Another thread is executing a commands loading command)");
            throw new ModuleLoadingException("Commands Module Unload: Canceled! (Reason: Another thread is executing a commands loading command)");
        }
        if (!isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Commands Module Unload: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("Commands Module Unload: Failed! (Reason: the module isn't loaded)");
        }

        isModuleLoaded = false;

        if(!disableLog) ServerLogger.getLogger().info("Commands Module Unload: Successful!");
    }
    public synchronized static void reload(@NotNull final JavaPlugin pluginInstance) throws ModuleException {
        reload(pluginInstance, false);
    }
    public synchronized static void reload(@NotNull final JavaPlugin pluginInstance, final boolean disableLog) throws ModuleException {

        if (areCommandsLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Commands Module Reload: Canceled! (Reason: Another thread is executing a commands loading command)");
            throw new ModuleLoadingException("Commands Module Reload: Canceled! (Reason: Another thread is executing a commands loading command)");
        }
        if (!isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Commands Module Reload: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("Commands Module Reload: Failed! (Reason: the module isn't loaded)");
        }

        try {
            unload(true);
        } catch (ModuleException e) {
            if (!disableLog) ServerLogger.getLogger().severe("Commands Module Reload: Failed! (Reason: the unload routine has failed)");
            throw new ModuleReloadingException("Commands Module Reload: Failed! (Reason: the unload routine has failed)", e);
        }

        try {
            load(pluginInstance, true);
        } catch (ModuleException e) {
            if (!disableLog) ServerLogger.getLogger().severe("Commands Module Reload: Failed! (Reason: the load routine has failed)");
            throw new ModuleReloadingException("Commands Module Reload: Failed! (Reason: the load routine has failed)", e);
        }

        if (!disableLog) ServerLogger.getLogger().info("Commands Module Reload: Successful!");
    }
    public static void sendDefaultError(@NotNull final CommandSender sender, @Nullable final Throwable e) {
        try {
            String err = LocalizationModule.translate(Defs.Localization.Keys.COMMAND_EXECUTION_ERROR);
            sender.sendMessage(ChatColor.RED + err);
            ServerLogger.getLogger().severe(err);
            if (e != null) ServerLogger.getLogger().severe(StringHandler.getStackTrace(e));
        } catch (Exception ignored) {}
    }
}

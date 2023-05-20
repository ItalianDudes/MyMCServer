package it.italiandudes.mymcserver.modules;

import it.italiandudes.idl.common.StringHandler;
import it.italiandudes.mymcserver.MyMCServer;
import it.italiandudes.mymcserver.commands.MyMCServerCommand;
import it.italiandudes.mymcserver.commands.modules.MMCSLoadCommand;
import it.italiandudes.mymcserver.commands.modules.MMCSReloadCommand;
import it.italiandudes.mymcserver.commands.modules.MMCSUnloadCommand;
import it.italiandudes.mymcserver.commands.remote.MMCSRemoteCommand;
import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleAlreadyLoadedException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleLoadingException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleNotLoadedException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleReloadingException;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.ServerLogger;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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
            registerCommand(MyMCServerCommand.COMMAND_NAME, new MyMCServerCommand());
            registerCommand(MMCSLoadCommand.COMMAND_NAME, new MMCSLoadCommand());
            registerCommand(MMCSUnloadCommand.COMMAND_NAME, new MMCSUnloadCommand());
            registerCommand(MMCSReloadCommand.COMMAND_NAME, new MMCSReloadCommand());
            registerCommand(MMCSRemoteCommand.COMMAND_NAME, new MMCSRemoteCommand());
        } catch (Exception e) {
            areCommandsLoading = false;
            if (!disableLog) ServerLogger.getLogger().severe("Commands Module Load: Failed! (Reason: an error has occurred on module loading)");
            throw new ModuleLoadingException("Commands Module Load: Failed! (Reason: an error has occurred on module loading)", e);
        }

        areCommandsLoading = false;
        isModuleLoaded = true;
        if (!disableLog) ServerLogger.getLogger().info("Commands Module Load: Successful!");
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
    private synchronized static void registerCommand(@NotNull final String COMMAND_NAME, @NotNull final CommandExecutor COMMAND) {
        Objects.requireNonNull(MyMCServer.getPluginInstance().getCommand(COMMAND_NAME)).setExecutor(COMMAND);
    }

    // Fake player to get the output of the command
    public static final class PlayerCommandSender implements CommandSender {

        // Attributes
        private final Player player;
        private final PrintStream printStream;

        // Constructors
        public PlayerCommandSender(@NotNull final Player player, @NotNull final PrintStream printStream) {
            this.player = player;
            this.printStream = printStream;
        }

        // Methods
        @Override
        public void sendMessage(@NotNull String message) {
            printStream.println(message);
        }
        @Override
        public void sendMessage(String[] messages) {
            for (String message : messages) {
                sendMessage(message);
            }
        }
        @Override
        public void sendMessage(@Nullable UUID sender, @NotNull String message) {
            player.sendMessage(sender, message);
        }
        @Override
        public void sendMessage(@Nullable UUID sender, @NotNull String... messages) {
            player.sendMessage(sender, messages);
        }
        @Override @NotNull
        public Server getServer() {
            return player.getServer();
        }
        @Override @NotNull
        public String getName() {
            return player.getName();
        }
        @Override @NotNull
        public Spigot spigot() {
            return player.spigot();
        }
        @Override
        public @NotNull Component name() {
            return player.name();
        }
        @Override
        public boolean isPermissionSet(@NotNull String name) {
            return player.isPermissionSet(name);
        }
        @Override
        public boolean isPermissionSet(@NotNull Permission perm) {
            return player.isPermissionSet(perm);
        }
        @Override
        public boolean hasPermission(@NotNull String name) {
            return player.hasPermission(name);
        }
        @Override
        public boolean hasPermission(@NotNull Permission perm) {
            return player.hasPermission(perm);
        }
        @Override @NotNull
        public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
            return player.addAttachment(plugin, name, value);
        }
        @Override @NotNull
        public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
            return player.addAttachment(plugin);
        }
        @Override
        public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
            return player.addAttachment(plugin, name, value, ticks);
        }
        @Override
        public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
            return player.addAttachment(plugin, ticks);
        }
        @Override
        public void removeAttachment(@NotNull PermissionAttachment attachment) {
            player.removeAttachment(attachment);
        }
        @Override
        public void recalculatePermissions() {
            player.recalculatePermissions();
        }
        @Override @NotNull
        public Set<PermissionAttachmentInfo> getEffectivePermissions() {
            return player.getEffectivePermissions();
        }
        @Override
        public boolean isOp() {
            return player.isOp();
        }
        @Override
        public void setOp(boolean value) {
            player.setOp(value);
        }
    }
}

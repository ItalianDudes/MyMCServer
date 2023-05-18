package it.italiandudes.mymcserver.commands.modules;

import it.italiandudes.mymcserver.MyMCServer;
import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.modules.*;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.Defs.Localization.Keys;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"deprecation", "DuplicatedCode", "unused"})
public final class MMCSLoadCommand implements CommandExecutor {

    // Attributes
    @NotNull public static final String COMMAND_NAME = Defs.Commands.MMCS_LOAD;
    public static final boolean RUN_WITH_MODULE_NOT_LOADED = true;

    // Command Body
    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if (!CommandsModule.isModuleLoaded() && !RUN_WITH_MODULE_NOT_LOADED) {
            try {
                sender.sendMessage(
                    ChatColor.RED +
                    LocalizationModule.translate(Keys.COMMAND_MODULE_NOT_LOADED)
                );
            } catch (ModuleException ignored) {}
            return true;
        }
        if (!sender.isOp()) {
            try {
                sender.sendMessage(
                    ChatColor.RED +
                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_MISSING_PERMISSIONS)
                );
            }catch (ModuleException ignored) {}
            return true;
        }
        if (args.length < 1) {
            try {
                sender.sendMessage(
                    ChatColor.RED +
                    LocalizationModule.translate(Keys.COMMAND_INSUFFICIENT_PARAMETERS)
                );
            }catch (ModuleException ignored) {}
            return true;
        }

        boolean commandError = false;
        for (int i = 0; i< args.length && !commandError; i++) {
            try {
                switch (args[i]) {
                    case Defs.ModuleNames.MODULE_DBCONNECTION -> {
                        if (i + 1 < args.length) {
                            i++;
                            try {
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    LocalizationModule.translate(Keys.COMMAND_LOADING_STARTED) +
                                    Defs.ModuleNames.MODULE_DBCONNECTION
                                );
                                DBConnectionModule.load(args[i], !(sender instanceof Player));
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    LocalizationModule.translate(Keys.COMMAND_LOADING_SUCCESS) +
                                    Defs.ModuleNames.MODULE_DBCONNECTION
                                );
                            } catch (ModuleException e) {
                                sender.sendMessage(
                                    ChatColor.RED +
                                    LocalizationModule.translate(Keys.COMMAND_LOADING_FAIL) +
                                    Defs.ModuleNames.MODULE_DBCONNECTION
                                );
                            }
                        }else {
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Keys.COMMAND_SYNTAX_ERROR)
                            );
                            commandError = true;
                        }
                    }

                    case Defs.ModuleNames.MODULE_LOCALIZATION -> {
                        if (i + 1 < args.length) {
                            i++;
                            try {
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    "Loading Module: " +
                                    Defs.ModuleNames.MODULE_LOCALIZATION
                                );
                                LocalizationModule.load(MyMCServer.getPluginInstance(), args[i], !(sender instanceof Player));
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    "Loading Module Success: " +
                                    Defs.ModuleNames.MODULE_LOCALIZATION
                                );
                            } catch (ModuleException e) {
                                sender.sendMessage(
                                    ChatColor.RED +
                                    "Loading Module Failed: " +
                                    Defs.ModuleNames.MODULE_LOCALIZATION
                                );
                            }
                        }else {
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Keys.COMMAND_SYNTAX_ERROR)
                            );
                            commandError = true;
                        }
                    }

                    case Defs.ModuleNames.MODULE_CONFIG -> {
                        try {
                            sender.sendMessage(
                                ChatColor.AQUA +
                                LocalizationModule.translate(Keys.COMMAND_LOADING_STARTED) +
                                Defs.ModuleNames.MODULE_CONFIG
                            );
                            ConfigModule.load(MyMCServer.getPluginInstance(), !(sender instanceof Player));
                            sender.sendMessage(
                                ChatColor.AQUA +
                                LocalizationModule.translate(Keys.COMMAND_LOADING_SUCCESS) +
                                Defs.ModuleNames.MODULE_CONFIG
                            );
                        } catch (ModuleException e) {
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Keys.COMMAND_LOADING_FAIL) +
                                Defs.ModuleNames.MODULE_CONFIG
                            );
                        }
                    }

                    case Defs.ModuleNames.MODULE_CONNECTION -> {
                        if (!DBConnectionModule.isModuleLoaded()) {
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Keys.COMMAND_LOADING_DEPENDENCIES_ARE_OFF)
                            );
                        }else if (i+1 < args.length) {
                            i++;
                            try {
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    LocalizationModule.translate(Keys.COMMAND_LOADING_STARTED) +
                                    Defs.ModuleNames.MODULE_CONNECTION
                                );
                                try {
                                    int port = Integer.parseInt(args[i]);
                                    if (port <= 0 || port >= 65535) throw new NumberFormatException();
                                    ConnectionModule.load(port, !(sender instanceof Player));
                                    sender.sendMessage(
                                        ChatColor.AQUA +
                                        LocalizationModule.translate(Keys.COMMAND_LOADING_SUCCESS) +
                                        Defs.ModuleNames.MODULE_CONNECTION
                                    );
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(
                                        ChatColor.RED +
                                        LocalizationModule.translate(Keys.COMMAND_SYNTAX_ERROR)
                                    );
                                    commandError = true;
                                }
                            } catch (ModuleException e) {
                                sender.sendMessage(
                                    ChatColor.RED +
                                    LocalizationModule.translate(Keys.COMMAND_LOADING_FAIL) +
                                    Defs.ModuleNames.MODULE_CONNECTION
                                );
                            }
                        }else {
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Keys.COMMAND_SYNTAX_ERROR)
                            );
                            commandError = true;
                        }
                    }

                    case Defs.ModuleNames.MODULE_COMMANDS -> {
                        try {
                            sender.sendMessage(
                                ChatColor.AQUA +
                                LocalizationModule.translate(Keys.COMMAND_LOADING_STARTED) +
                                Defs.ModuleNames.MODULE_COMMANDS
                            );
                            CommandsModule.load(MyMCServer.getPluginInstance(), !(sender instanceof Player));
                            sender.sendMessage(
                                ChatColor.AQUA +
                                LocalizationModule.translate(Keys.COMMAND_LOADING_SUCCESS) +
                                Defs.ModuleNames.MODULE_COMMANDS
                            );
                        } catch (ModuleException e) {
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Keys.COMMAND_LOADING_FAIL) +
                                Defs.ModuleNames.MODULE_COMMANDS
                            );
                        }
                    }

                    default -> sender.sendMessage(
                        ChatColor.RED +
                        LocalizationModule.translate(Keys.COMMAND_SYNTAX_ERROR)
                    );
                }
            } catch (ModuleException e) {
                CommandsModule.sendDefaultError(sender, e);
            }
        }

        return true;
    }
}

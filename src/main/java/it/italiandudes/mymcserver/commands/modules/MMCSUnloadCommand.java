package it.italiandudes.mymcserver.commands.modules;

import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.modules.*;
import it.italiandudes.mymcserver.utils.Defs;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"deprecation", "DuplicatedCode", "unused"})
public final class MMCSUnloadCommand implements CommandExecutor {

    // Attributes
    public static final String COMMAND_NAME = Defs.Commands.MMCS_UNLOAD;
    public static final boolean RUN_WITH_MODULE_NOT_LOADED = true;

    // Subcommand Body
    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if (!CommandsModule.isModuleLoaded() && !RUN_WITH_MODULE_NOT_LOADED) {
            try {
                sender.sendMessage(
                    ChatColor.RED +
                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_MODULE_NOT_LOADED)
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
                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_INSUFFICIENT_PARAMETERS)
                );
            }catch (ModuleException ignored) {}
            return true;
        }

        for (String module : args) {
            try {
                switch (module) {
                    case Defs.ModuleNames.MODULE_DBCONNECTION -> {
                        if (DBConnectionModule.isModuleLoaded()) {
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_DEPENDANT_ARE_ON)
                            );
                        }else {
                            try {
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_STARTED) +
                                    Defs.ModuleNames.MODULE_DBCONNECTION
                                );
                                DBConnectionModule.unload(!(sender instanceof Player));
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_SUCCESS) +
                                    Defs.ModuleNames.MODULE_DBCONNECTION
                                );
                            } catch (ModuleException e) {
                                sender.sendMessage(
                                    ChatColor.RED +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_FAIL) +
                                    Defs.ModuleNames.MODULE_DBCONNECTION
                                );
                            }
                        }
                    }

                    case Defs.ModuleNames.MODULE_LOCALIZATION -> {
                        try {
                            sender.sendMessage(
                                ChatColor.AQUA +
                                "Unloading Module: " +
                                Defs.ModuleNames.MODULE_LOCALIZATION
                            );
                            LocalizationModule.unload(!(sender instanceof Player));
                            sender.sendMessage(
                                ChatColor.AQUA +
                                "Unloading Module Success: " +
                                Defs.ModuleNames.MODULE_LOCALIZATION
                            );
                        } catch (ModuleException e) {
                            sender.sendMessage(
                                ChatColor.RED +
                                "Unloading Module Failed: " +
                                Defs.ModuleNames.MODULE_LOCALIZATION
                            );
                        }
                    }

                    case Defs.ModuleNames.MODULE_CONFIG -> {
                        try {
                            sender.sendMessage(
                                ChatColor.AQUA +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_STARTED) +
                                Defs.ModuleNames.MODULE_CONFIG
                            );
                            ConfigModule.unload(!(sender instanceof Player));
                            sender.sendMessage(
                                ChatColor.AQUA +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_SUCCESS) +
                                Defs.ModuleNames.MODULE_CONFIG
                            );
                        } catch (ModuleException e) {
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_FAIL) +
                                Defs.ModuleNames.MODULE_CONFIG
                            );
                        }
                    }

                    case Defs.ModuleNames.MODULE_CONNECTION -> {
                        try {
                            sender.sendMessage(
                                ChatColor.AQUA +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_STARTED) +
                                Defs.ModuleNames.MODULE_CONNECTION
                            );
                            ConnectionModule.unload(!(sender instanceof Player));
                            sender.sendMessage(
                                ChatColor.AQUA +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_SUCCESS) +
                                Defs.ModuleNames.MODULE_CONNECTION
                            );
                        } catch (ModuleException e) {
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_FAIL) +
                                Defs.ModuleNames.MODULE_CONNECTION
                            );
                        }
                    }

                    case Defs.ModuleNames.MODULE_COMMANDS -> {
                        try {
                            sender.sendMessage(
                                ChatColor.AQUA +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_STARTED) +
                                Defs.ModuleNames.MODULE_COMMANDS
                            );
                            CommandsModule.unload(!(sender instanceof Player));
                            sender.sendMessage(
                                ChatColor.AQUA +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_SUCCESS) +
                                Defs.ModuleNames.MODULE_COMMANDS
                            );
                        } catch (ModuleException e) {
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_UNLOADING_FAIL) +
                                Defs.ModuleNames.MODULE_COMMANDS
                            );
                        }
                    }

                    default -> sender.sendMessage(
                        ChatColor.RED +
                        LocalizationModule.translate(Defs.Localization.Keys.COMMAND_SYNTAX_ERROR)
                    );
                }
            } catch (ModuleException e) {
                CommandsModule.sendDefaultError(sender, e);
            }
        }

        return true;
    }
}

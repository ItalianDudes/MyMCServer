package it.italiandudes.mymcserver.commands;

import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.modules.*;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.Defs.Localization.Keys;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"deprecation", "unused"})
public final class MyMCServerCommand implements CommandExecutor {

    // Command Name
    public static final String COMMAND_NAME = Defs.Commands.COMMAND_NAME[0];
    public static final boolean RUN_WITH_MODULE_NOT_LOADED = true;

    // Command Arguments
    public static final class Arguments {
        public static final String INFO = "info";
        public static final String VERSION = "version";
        public static final String MODULE_STATUS = "modstats";
    }

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
        if (args.length < 1) return false;

        try {

            switch (args[0].toLowerCase()) {
                case Arguments.INFO -> sender.sendMessage(
                        ChatColor.AQUA +
                        LocalizationModule.translate(Keys.MMCS_INFO)
                );

                case Arguments.VERSION -> sender.sendMessage(
                    ChatColor.AQUA +
                    LocalizationModule.translate(Keys.MMCS_VERSION) +
                    Defs.PluginInfo.PLUGIN_VERSION
                );

                case Arguments.MODULE_STATUS -> {
                    sender.sendMessage(
                        ChatColor.AQUA +
                        Defs.ModuleNames.MODULE_CONFIG +
                        ": "+(ConfigModule.isModuleLoaded()?"ONLINE":"OFFLINE")
                    );
                    sender.sendMessage(
                        ChatColor.AQUA +
                        Defs.ModuleNames.MODULE_LOCALIZATION +
                        ": "+(LocalizationModule.isModuleLoaded()?"ONLINE":"OFFLINE")
                    );
                    sender.sendMessage(
                        ChatColor.AQUA +
                        Defs.ModuleNames.MODULE_DBCONNECTION +
                        ": "+(DBConnectionModule.isModuleLoaded()?"ONLINE":"OFFLINE")
                    );
                    sender.sendMessage(
                        ChatColor.AQUA +
                        Defs.ModuleNames.MODULE_COMMANDS +
                        ": "+(CommandsModule.isModuleLoaded()?"ONLINE":"OFFLINE")
                    );
                    sender.sendMessage(
                        ChatColor.AQUA +
                        Defs.ModuleNames.MODULE_CONNECTION +
                        ": "+(ConnectionModule.isModuleLoaded()?"ONLINE":"OFFLINE")
                    );
                }

                default -> sender.sendMessage(
                    ChatColor.RED +
                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_SYNTAX_ERROR)
                );
            }

        } catch (ModuleException e) {
            CommandsModule.sendDefaultError(sender, e);
        }

        return true;
    }
}

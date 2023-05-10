package it.italiandudes.mymcserver;

import it.italiandudes.idl.common.StringHandler;
import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.modules.CommandsModule;
import it.italiandudes.mymcserver.modules.ConfigModule;
import it.italiandudes.mymcserver.modules.LocalizationModule;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.ServerLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public final class MyMCServer extends JavaPlugin {

    // Attributes
    private boolean instanceErrored;
    private static JavaPlugin pluginInstance = null;

    // Plugin Startup
    @Override
    public void onEnable() {

        // Instance Errored Flag
        instanceErrored = false;
        pluginInstance = this;

        try {

            // Load Configs from Files
            loadConfigs(this);

            // Load Langs from Config's selected Lang
            loadLangs(this);

            ServerLogger.getLogger().info("All core modules are loaded, loading plugin modules...");

            // Load Commands
            CommandsModule.load(this);

        }catch (Exception e) {
            ServerLogger.getLogger().severe("An unhandled exception has reached the function, shutting down the plugin...");
            ServerLogger.getLogger().severe("Exception stacktrace:");
            ServerLogger.getLogger().severe(StringHandler.getStackTrace(e));
            onDisable();
        }
    }

    // Plugin Shutdown
    @Override
    public void onDisable() {
        if (instanceErrored) return;
        instanceErrored = true;
        try {
            CommandsModule.unload(true);
        } catch (ModuleException ignored) {}
        ServerLogger.getLogger().info("DB Connection Module Unload: Successful!");
        try {
            LocalizationModule.unload(true);
        } catch (ModuleException ignored) {}
        ServerLogger.getLogger().info("Localization Module Unload: Successful!");
        try {
            ConfigModule.unload(true);
        } catch (ModuleException ignored) {}
        ServerLogger.getLogger().info("Config Module Unload: Successful!");
    }

    // Instance Getter
    @NotNull
    public static JavaPlugin getPluginInstance() {
        return pluginInstance;
    }

    // Methods
    private void loadConfigs(@NotNull JavaPlugin pluginInstance) throws ModuleException {
        ConfigModule.load(pluginInstance);
    }
    private void loadLangs(@NotNull JavaPlugin pluginInstance) throws ModuleException {
        LocalizationModule.load(pluginInstance, Objects.requireNonNull(ConfigModule.getConfig(Defs.Config.Identifiers.GENERAL_CONFIG, Defs.Config.Keys.General.KEY_LANG)));
    }
}

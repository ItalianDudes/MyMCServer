package it.italiandudes.mymcserver;

import it.italiandudes.idl.common.StringHandler;
import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleMissingConfigsException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleMissingDependenciesException;
import it.italiandudes.mymcserver.modules.CommandsModule;
import it.italiandudes.mymcserver.modules.ConfigModule;
import it.italiandudes.mymcserver.modules.DBConnectionModule;
import it.italiandudes.mymcserver.modules.LocalizationModule;
import it.italiandudes.mymcserver.modules.connection.ConnectionModule;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.Resource;
import it.italiandudes.mymcserver.utils.ServerLogger;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings({"unused", "deprecation"})
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

            // Load Commands
            CommandsModule.load(this);

            ServerLogger.getLogger().info("All core modules are loaded, loading plugin modules...");

            // Load the DBConnection module using configs
            loadDB();

            // Load Connection using DBConnection and configs
            loadConnection();

            ServerLogger.getLogger().info(ChatColor.AQUA +"All modules are loaded, plugin fully operational");

        }catch (Exception e) {
            ServerLogger.getLogger().severe("An unhandled exception has reached the top of the stack, shutting down the plugin...");
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
            ConnectionModule.unload(true);
        } catch (ModuleException ignored) {}
        ServerLogger.getLogger().info(ChatColor.AQUA+ "Connection Module Unload: Successful!");
        try {
            DBConnectionModule.unload(true);
        } catch (ModuleException ignored) {}
        ServerLogger.getLogger().info(ChatColor.AQUA+ "DB Connection Module Unload: Successful!");
        try {
            CommandsModule.unload(true);
        } catch (ModuleException ignored) {}
        ServerLogger.getLogger().info(ChatColor.AQUA+ "Commands Module Unload: Successful!");
        try {
            LocalizationModule.unload(true);
        } catch (ModuleException ignored) {}
        ServerLogger.getLogger().info(ChatColor.AQUA+ "Localization Module Unload: Successful!");
        try {
            ConfigModule.unload(true);
        } catch (ModuleException ignored) {}
        ServerLogger.getLogger().info(ChatColor.AQUA+ "Config Module Unload: Successful!");
    }

    // Instance Getter
    @NotNull
    public static JavaPlugin getPluginInstance() {
        return pluginInstance;
    }
    @NotNull
    public static Server getServerInstance() {
        return pluginInstance.getServer();
    }

    // Methods
    private void loadConfigs(@NotNull JavaPlugin pluginInstance) throws ModuleException {
        ConfigModule.load(pluginInstance);
    }
    private void loadLangs(@NotNull JavaPlugin pluginInstance) throws ModuleException {
        LocalizationModule.load(pluginInstance, Objects.requireNonNull(ConfigModule.getConfig(Defs.Config.Identifiers.GENERAL_CONFIG, Defs.Config.Keys.General.KEY_LANG)));
    }
    private void loadDB() throws ModuleException {
        try {
            DBConnectionModule.load(Objects.requireNonNull(ConfigModule.getConfig(Defs.Config.Identifiers.GENERAL_CONFIG, Defs.Config.Keys.General.KEY_DATABASE_URL)));
        } catch (ModuleMissingConfigsException e) {
            return;
        }
            String dbType = DBConnectionModule.getConnectorType();
            String query = DBConnectionModule.getQueryFromResourcesFileSQL(Resource.Path.DBConnection.SQL_DIR + dbType + Resource.Path.DBConnection.SQL_FILE_EXTENSION);
            DBConnectionModule.executePreparedStatementFromQueryIgnoreResult(query);
    }
    private void loadConnection() throws ModuleException {
        try {
            ConnectionModule.load(Integer.parseInt(Objects.requireNonNull(ConfigModule.getConfig(Defs.Config.Identifiers.GENERAL_CONFIG, Defs.Config.Keys.General.KEY_CONNECTION_PORT))));
        } catch (ModuleMissingDependenciesException ignored) {}
    }
}

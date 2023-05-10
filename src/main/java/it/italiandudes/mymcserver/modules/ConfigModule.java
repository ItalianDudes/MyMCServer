package it.italiandudes.mymcserver.modules;

import it.italiandudes.idl.common.JarHandler;
import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleAlreadyLoadedException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleLoadingException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleNotLoadedException;
import it.italiandudes.mymcserver.exceptions.modules.ModuleReloadingException;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.Resource;
import it.italiandudes.mymcserver.utils.ServerLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@SuppressWarnings("unused")
public final class ConfigModule {

    // Attributes
    private static JSONObject generalConfigFile = null;
    private static boolean areConfigsLoading = false;

    // Default Constructor
    public ConfigModule() {
        throw new RuntimeException("Can't instantiate this class!");
    }

    // Module Checker
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isModuleLoaded() {
        return generalConfigFile != null;
    }

    // Methods
    public synchronized static void load(@NotNull final JavaPlugin pluginInstance) throws ModuleException {
        load(pluginInstance, false);
    }
    public synchronized static void load(@NotNull final JavaPlugin pluginInstance, final boolean disableLog) throws ModuleException {

        if (areConfigsLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Config Module Load: Canceled! (Reason: Another thread is executing a config loading command)");
            throw new ModuleLoadingException("Config Module Load: Canceled! (Reason: Another thread is executing a config loading command)");
        }
        if (isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Config Module Load: Failed! (Reason: the module has already been loaded)");
            throw new ModuleAlreadyLoadedException("Config Module Load: Failed! (Reason: the module has already been loaded)");
        }

        areConfigsLoading = true;

        if (!new File(pluginInstance.getDataFolder().getAbsolutePath()+ Resource.Path.Config.CONFIG_DIR).exists()) {
            try {
                if (!pluginInstance.getDataFolder().exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    pluginInstance.getDataFolder().mkdirs();
                }
                JarHandler.copyDirectoryFromJar(new File(Defs.PluginInfo.PLUGIN_JAR_PATH), Resource.Path.Config.CONFIG_DIR, pluginInstance.getDataFolder(), false, false);
            } catch (IOException e) {
                areConfigsLoading = false;
                if (!disableLog) ServerLogger.getLogger().severe("Config Module Load: Failed! (Reason: Config file copy failed)");
                throw new ModuleLoadingException("Config Module Load: Failed! (Reason: Config file copy failed)");
            }
        }

        // General Config File
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(pluginInstance.getDataFolder().getAbsolutePath()+Resource.Path.Config.GENERAL_CONFIG);
            generalConfigFile = (JSONObject) new JSONParser().parse(fileReader);
            fileReader.close();
        } catch (IOException | ParseException e) {
            areConfigsLoading = false;
            try {
                if (fileReader != null) fileReader.close();
            }catch (Exception ignored) {}
            if (!disableLog) ServerLogger.getLogger().severe("Config Module Load: Failed! (Reason: an error has occurred on config file reading/parsing)");
            throw new ModuleLoadingException("Config Module Load: Failed! (Reason: an error has occurred on config file reading/parsing)");
        }

        if (!disableLog) ServerLogger.getLogger().info("Config Module Load: Successful!");
        areConfigsLoading = false;
    }
    public synchronized static void unload() throws ModuleException {
        unload(false);
    }
    public synchronized static void unload(final boolean disableLog) throws ModuleException {

        if (areConfigsLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Config Module Unload: Canceled! (Reason: Another thread is executing a config loading command)");
            throw new ModuleLoadingException("Config Module Unload: Canceled! (Reason: Another thread is executing a config loading command)");
        }
        if (!isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Config Module Unload: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("Config Module Unload: Failed! (Reason: the module isn't loaded)");
        }

        generalConfigFile = null;

        if(!disableLog) ServerLogger.getLogger().info("Config Module Unload: Successful!");
    }
    public synchronized static void reload(@NotNull final JavaPlugin pluginInstance) throws ModuleException {
        reload(pluginInstance, false);
    }
    public synchronized static void reload(@NotNull final JavaPlugin pluginInstance, final boolean disableLog) throws ModuleException {

        if (areConfigsLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Config Module Reload: Canceled! (Reason: Another thread is executing a config loading command)");
            throw new ModuleLoadingException("Config Module Reload: Canceled! (Reason: Another thread is executing a config loading command)");
        }
        if (!isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Config Module Reload: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("Config Module Reload: Failed! (Reason: the module isn't loaded)");
        }

        // Do current config backups
        JSONObject generalConfigFileBACKUP = generalConfigFile;

        try {
            unload(true);
        } catch (ModuleException e) {
            if (!disableLog) ServerLogger.getLogger().severe("Config Module Reload: Failed! (Reason: the unload routine has failed)");

            // Put config backups online again
            generalConfigFile = generalConfigFileBACKUP;

            throw new ModuleReloadingException("Config Module Reload: Failed! (Reason: the unload routine has failed)", e);
        }

        try {
            load(pluginInstance, true);
        } catch (ModuleException e) {
            if (!disableLog) ServerLogger.getLogger().severe("Config Module Reload: Failed! (Reason: the load routine has failed)");

            // Put config backups online again
            generalConfigFile = generalConfigFileBACKUP;

            throw new ModuleReloadingException("Config Module Reload: Failed! (Reason: the load routine has failed)", e);
        }

        if (!disableLog) ServerLogger.getLogger().info("Config Module Reload: Successful!");
    }
    @Nullable
    public synchronized static String getConfig(@NotNull final String CONFIG_TYPE, @NotNull final String KEY) throws ModuleException {

        if (areConfigsLoading) {
            ServerLogger.getLogger().warning("GetConfig Operation: Canceled! (Reason: Another thread is executing a config loading command)");
            throw new ModuleLoadingException("GetConfig Operation: Canceled! (Reason: Another thread is executing a config loading command)");
        }
        if (!isModuleLoaded()) {
            ServerLogger.getLogger().severe("GetConfig Operation: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("GetConfig Operation: Failed! (Reason: the module isn't loaded)");
        }

        //noinspection SwitchStatementWithTooFewBranches
        switch (CONFIG_TYPE) {
            case Defs.Config.Identifiers.GENERAL_CONFIG -> {
                return (String) generalConfigFile.get(KEY);
            }
            default -> {
                return null;
            }
        }
    }
}

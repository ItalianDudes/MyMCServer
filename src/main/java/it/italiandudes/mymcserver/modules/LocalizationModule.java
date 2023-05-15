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
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unused")
public final class LocalizationModule {

    // Attributes
    private static JSONObject langFile = null;
    private static boolean areLangsLoading = false;

    // Default Constructor
    public LocalizationModule() {
        throw new RuntimeException("Can't instantiate this class!");
    }

    // Module Checker
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isModuleLoaded() {
        return langFile != null;
    }

    // Localized String Getter
    public synchronized static void load(@NotNull final JavaPlugin pluginInstance, @NotNull final String LOCALIZATION) throws ModuleException {
        load(pluginInstance, LOCALIZATION, false);
    }
    public synchronized static void load(@NotNull final JavaPlugin pluginInstance, @NotNull final String LOCALIZATION, final boolean disableLog) throws ModuleException {

        if (areLangsLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Localization Module Load: Canceled! (Reason: Another thread is executing a langs loading command)");
            throw new ModuleLoadingException("Localization Module Load: Canceled! (Reason: Another thread is executing a langs loading command)");
        }
        if (isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Localization Module Load: Failed! (Reason: the module has already been loaded)");
            throw new ModuleAlreadyLoadedException("Localization Module Load: Failed! (Reason: the module has already been loaded)");
        }

        areLangsLoading = true;

        if (!new File(pluginInstance.getDataFolder().getAbsolutePath()+ Resource.Path.Localization.LOCALIZATION_DIR).exists()) {
            try {
                if (!pluginInstance.getDataFolder().exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    pluginInstance.getDataFolder().mkdirs();
                }
                JarHandler.copyDirectoryFromJar(new File(Defs.PluginInfo.PLUGIN_JAR_PATH), Resource.Path.Localization.LOCALIZATION_DIR, pluginInstance.getDataFolder(), false, false);
            } catch (IOException e) {
                areLangsLoading = false;
                if (!disableLog) ServerLogger.getLogger().severe("Localization Module Load: Failed! (Reason: Localization file copy failed)");
                throw new ModuleLoadingException("Localization Module Load: Failed! (Reason: Localization file copy failed)");
            }
        }

        File filepath = new File(pluginInstance.getDataFolder().getAbsolutePath()+Resource.Path.Localization.LOCALIZATION_DIR+LOCALIZATION+".json");
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(filepath, StandardCharsets.UTF_8);
            langFile = (JSONObject) new JSONParser().parse(fileReader);
            fileReader.close();
        } catch (IOException | ParseException e) {
            areLangsLoading = false;
            try {
                if (fileReader != null) fileReader.close();
            } catch (Exception ignored){}
            if (!disableLog) ServerLogger.getLogger().severe("Localization Module Load: Failed! (Reason: an error has occurred on localization file reading/parsing)");
            throw new ModuleLoadingException("Localization Module Load: Failed! (Reason: an error has occurred on localization file reading/parsing)");
        }

        if (!disableLog) ServerLogger.getLogger().info("Localization Module Load: Successful!");
        areLangsLoading = false;
    }
    public synchronized static void unload() throws ModuleException {
        unload(false);
    }
    public synchronized static void unload(final boolean disableLog) throws ModuleException {

        if (areLangsLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Localization Module Unload: Canceled! (Reason: Another thread is executing a langs loading command)");
            throw new ModuleLoadingException("Localization Module Unload: Canceled! (Reason: Another thread is executing a langs loading command)");
        }
        if (!isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Localization Module Unload: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("Localization Module Unload: Failed! (Reason: the module isn't loaded)");
        }

        langFile = null;
        if (!disableLog) ServerLogger.getLogger().info("Localization Module Unload: Successful!");
    }
    public synchronized static void reload(@NotNull final JavaPlugin pluginInstance, @NotNull final String LOCALIZATION) throws ModuleException {
        reload(pluginInstance, LOCALIZATION, false);
    }
    public synchronized static void reload(@NotNull final JavaPlugin pluginInstance, @NotNull final String LOCALIZATION, final boolean disableLog) throws ModuleException {

        if (areLangsLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Localization Module Reload: Canceled! (Reason: Another thread is executing a langs loading command)");
            throw new ModuleLoadingException("Localization Module Reload: Canceled! (Reason: Another thread is executing a langs loading command)");
        }
        if (!isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Localization Module Reload: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("Localization Module Reload: Failed! (Reason: the module isn't loaded)");
        }

        // Do current lang backup
        JSONObject langFileBACKUP = langFile;

        try {
            unload(true);
        } catch (ModuleException e) {
            if (!disableLog) ServerLogger.getLogger().severe("Localization Module Reload: Failed! (Reason: the unload routine has failed)");

            // Put lang backup online again
            langFile= langFileBACKUP;

            throw new ModuleReloadingException("Localization Module Reload: Failed! (Reason: the unload routine has failed)", e);
        }

        try {
            load(pluginInstance, LOCALIZATION, true);
        } catch (ModuleException e) {
            if (!disableLog) ServerLogger.getLogger().severe("Localization Module Reload: Failed! (Reason: the load routine has failed)");

            // Put lang backup online again
            langFile= langFileBACKUP;

            throw new ModuleReloadingException("Localization Module Reload: Failed! (Reason: the load routine has failed)", e);
        }

        if (!disableLog) ServerLogger.getLogger().info("Localization Module Reload: Successful!");
    }
    @Nullable
    public synchronized static String translate(@NotNull final String KEY) throws ModuleException {

        if (areLangsLoading) {
            ServerLogger.getLogger().warning("Translate Operation: Canceled! (Reason: Another thread is executing a langs loading command)");
            throw new ModuleLoadingException("Translate Operation: Canceled! (Reason: Another thread is executing a langs loading command)");
        }
        if (!isModuleLoaded()) {
            ServerLogger.getLogger().severe("Translate Operation: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("Translate Operation: Failed! (Reason: the module isn't loaded)");
        }

        return (String) langFile.get(KEY);
    }
}

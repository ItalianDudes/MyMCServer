package it.italiandudes.mymcserver.utils;

import it.italiandudes.mymcserver.MyMCServer;

import java.io.File;
import java.net.URISyntaxException;

@SuppressWarnings("unused")
public final class Defs {

    // Plugin Info
    public static final class PluginInfo {
        public static final String PLUGIN_JAR_PATH;
        static {
            try {
                PLUGIN_JAR_PATH = new File(MyMCServer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        public static final String PLUGIN_VERSION = "0.0.1A";
    }

    // Module Names
    public static final class ModuleNames {
        public static final String MODULE_LOCALIZATION = "localization";
        public static final String MODULE_CONFIG = "config";
        public static final String MODULE_DBCONNECTION = "dbconnection";
        public static final String MODULE_CONNECTION = "connection";
        public static final String MODULE_COMMANDS = "commands";
    }

    // Config Constants
    public static final class Config {

        // Config Files Identifier
        public static final class Identifiers {
            public static final String GENERAL_CONFIG = "general";
        }

        // Config Keys
        public static final class Keys {

            // General Config File
            public static final class General {
                public static final String KEY_LANG = "language_pack";
                public static final String KEY_DATABASE_URL = "database_url";
            }
        }
    }

    // Localization Constants
    public static final class Localization {

        // Langs
        public static final class Langs {
            public static final String EN_US = "en-US";
            public static final String IT_IT = "it-IT";
            public static final String FALLBACK_LANGUAGE = EN_US;
        }

        // Lang Keys
        public static final class Keys {
            public static final String TEST_ENTRY = "test_entry";
            public static final String EE_VERSION = "ee_version";
            public static final String EE_INFO = "ee_info";
            public static final String COMMAND_EXECUTION_ERROR = "command_execution_error";
            public static final String COMMAND_SYNTAX_ERROR = "command_syntax_error";
            public static final String COMMAND_LOADING_STARTED = "command_loading_started";
            public static final String COMMAND_LOADING_SUCCESS = "command_loading_success";
            public static final String COMMAND_LOADING_FAIL = "command_loading_fail";
            public static final String COMMAND_UNLOADING_STARTED = "command_unloading_started";
            public static final String COMMAND_UNLOADING_SUCCESS = "command_unloading_success";
            public static final String COMMAND_UNLOADING_FAIL = "command_unloading_fail";
            public static final String COMMAND_RELOADING_STARTED = "command_reloading_started";
            public static final String COMMAND_RELOADING_SUCCESS = "command_reloading_success";
            public static final String COMMAND_RELOADING_FAIL = "command_reloading_fail";
            public static final String COMMAND_MODULE_NOT_LOADED = "command_module_not_loaded";
            public static final String COMMAND_MISSING_PERMISSIONS = "command_missing_permissions";
        }
    }

    // DBConnection Constants
    public static final class DBConnection {
        public static final String JDBC_MYSQL_CONNECTOR_STRING_START = "jdbc:mysql";
        public static final String JDBC_SQLITE_CONNECTOR_STRING_START = "jdbc:sqlite";
        public static final String JDBC_POSTGRESQL_CONNECTOR_STRING_START = "jdbc:postgresql";
        public static final String MYSQL_CONNECTOR = "mysql";
        public static final String SQLITE_CONNECTOR = "sqlite";
        public static final String POSTGRESQL_CONNECTOR = "postgresql";
    }

    // Command Constants
    public static final class Commands {
        public static final class MyMCServer {
            public static final String[] COMMAND_NAME = {"mymcserver", "mmcs"};
            public static final String EE_LOAD = "load";
            public static final String EE_UNLOAD = "unload";
            public static final String EE_RELOAD = "reload";
        }
    }
}

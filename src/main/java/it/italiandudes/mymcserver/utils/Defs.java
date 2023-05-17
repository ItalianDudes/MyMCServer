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
                public static final String KEY_CONNECTION_PORT = "connection_port";
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
            public static final String MMCS_VERSION = "mmcs_version";
            public static final String MMCS_INFO = "mmcs_info";
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
            public static final String COMMAND_UNLOADING_DEPENDANT_ARE_ON = "dependant_module_are_on";
            public static final String COMMAND_LOADING_DEPENDENCIES_ARE_OFF = "dependencies_module_are_off";
            public static final String COMMAND_RELOADING_MODULE_IS_OFF = "command_reloading_module_is_off";
            public static final String COMMAND_INSUFFICIENT_PARAMETERS = "command_insufficient_parameters";
            public static final String CONNECTION_RESPONSE_MESSAGE_BAD_REQUEST = "connection_response_message_bad_request";
            public static final String CONNECTION_RESPONSE_MESSAGE_UNAUTHORIZED = "connection_response_message_unauthorized";
            public static final String CONNECTION_RESPONSE_MESSAGE_FORBIDDEN = "connection_response_message_forbidden";
            public static final String CONNECTION_RESPONSE_MESSAGE_NOT_FOUND = "connection_response_message_not_found";
            public static final String CONNECTION_RESPONSE_MESSAGE_INTERNAL_SERVER_ERROR = "connection_response_message_internal_server_error";
            public static final String CONNECTION_RESPONSE_MESSAGE_LOGIN_SUCCESS = "connection_response_message_login_success";
        }
    }

    // DBConnection Constants
    public static final class DBConnection {
        public static final String JDBC_DB_UNCONFIGURED = "unconfigured";
        public static final String JDBC_MYSQL_CONNECTOR_STRING_START = "jdbc:mysql";
        public static final String JDBC_SQLITE_CONNECTOR_STRING_START = "jdbc:sqlite";
        public static final String JDBC_POSTGRESQL_CONNECTOR_STRING_START = "jdbc:postgresql";
        public static final String MYSQL_CONNECTOR = "mysql";
        public static final String SQLITE_CONNECTOR = "sqlite";
        public static final String POSTGRESQL_CONNECTOR = "postgresql";
    }

    // Command Constants
    public static final class Commands {
        public static final String MMCS_LOAD = "mmcsload";
        public static final String MMCS_UNLOAD = "mmcsunload";
        public static final String MMCS_RELOAD = "mmcsreload";
        public static final String[] COMMAND_NAME = {"mymcserver", "mmcs"};
    }

    // Connection Constants
    public static final class Connection {
        public static final int BACKLOG = 5;
        public static final int STOP_DELAY_SECONDS = 5;
        public static final class Context {
            private static final String CONTEXT_ROOT = "/";
            public static final String CONTEXT_COMMAND = CONTEXT_ROOT + "command";
            public static final String CONTEXT_STATS = CONTEXT_ROOT + "stats";
            public static final String CONTEXT_LOGIN = CONTEXT_ROOT + "login";
        }
        public static final class ReturnCode {
            public static final int OK = 200;
            public static final int BAD_REQUEST = 400;
            public static final int UNAUTHORIZED = 401;
            public static final int FORBIDDEN = 403;
            public static final int NOT_FOUND = 404;
            public static final int INTERNAL_SERVER_ERROR = 500;
        }
        public static final class JSONContent {
            public static final String RETURN_CODE = "return-code";
            public static final String TOKEN = "token";
            public static final String MESSAGE = "message";
            public static final String CPU_PERC = "cpu-perc";
            public static final String MAX_MEMORY = "max-memory";
            public static final String USED_MEMORY = "used-memory";
            public static final String PLAYER_LIST = "player-list";
            public static final String ADDONS_LIST = "addons-list";
            public static final class PlayerList {
                public static final String NAME = "name";
                public static final String ONLINE = "online";
            }
            public static final class AddonsList {
                public static final String NAME = "name";
                public static final String ENABLED = "enabled";
            }
        }
        public static final class Header {
            public static final String HEADER_USERNAME = "MMCS-Username";
            public static final String HEADER_PASSWORD = "MMCS-Password";
            public static final String HEADER_TOKEN = "MMCS-Token";
            public static final String HEADER_COMMAND = "MMCS-Command";
        }
    }

    // Protocol
    public static final class Protocol {
        public static final class StartingProtocol {
            public static final String STARTING_PROTOCOL_LOGIN = "login";
        }
        public static final String PROTOCOL_COMMAND = "command";
        public static final String PROTOCOL_SSH = "ssh";
        public static final String PROTOCOL_DISCONNECT = "disconnect";
    }

    // Executable Plugin
    public static final class ExecutablePlugin {
        public static final class ServerConsole {
            public static final String SERVER_CONSOLE_EXIT = "exit";
        }
    }
}

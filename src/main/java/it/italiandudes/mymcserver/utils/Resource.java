package it.italiandudes.mymcserver.utils;

import it.italiandudes.mymcserver.MyMCServer;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

@SuppressWarnings("unused")
public final class Resource {

    // Default Constructor
    public Resource() {
        throw new RuntimeException("Can't instantiate this class!");
    }

    //Resource Getter
    public static URL getResource(@NotNull final String resourceConst) {
        return Objects.requireNonNull(MyMCServer.class.getResource(resourceConst));
    }
    public static InputStream getResourceAsStream(@NotNull final String resourceConst) {
        return Objects.requireNonNull(MyMCServer.class.getResourceAsStream(resourceConst));
    }

    // Localization Directory
    public static final class Path {
        public static final String RESOURCE_DIR = "/";
        public static final class Localization {
            public static final String LOCALIZATION_DIR = RESOURCE_DIR+"localization/";
        }
        public static final class Config {
            public static final String CONFIG_DIR = RESOURCE_DIR+"config/";
            public static final String GENERAL_CONFIG = CONFIG_DIR+"general.json";
        }
        public static final class DBConnection {
            public static final String SQL_DIR = RESOURCE_DIR+"sql/";
            public static final String SQL_FILE_EXTENSION = ".sql";
            public static final String MYSQL_QUERY_PATH = SQL_DIR+"mysql"+SQL_FILE_EXTENSION;
            public static final String SQLITE_QUERY_PATH = SQL_DIR+"sqlite"+SQL_FILE_EXTENSION;
        }
    }
}

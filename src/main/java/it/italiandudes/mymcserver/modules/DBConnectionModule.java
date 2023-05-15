package it.italiandudes.mymcserver.modules;

import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.exceptions.modules.*;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.Resource;
import it.italiandudes.mymcserver.utils.ServerLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Scanner;

@SuppressWarnings("unused")
public final class DBConnectionModule {

    // Attributes
    private static Connection dbConnection = null;
    private static boolean isDBConnecting = false;
    @Nullable private static String dbType = null;

    // Default Constructor
    public DBConnectionModule() {
        throw new RuntimeException("Can't instantiate this class!");
    }

    // Query Retriever
    public static String getQueryFromResourcesFileSQL(@NotNull final String QUERY_PATH) {
        InputStream inStream = Resource.getResourceAsStream(QUERY_PATH);

        StringBuilder queryBuilder = new StringBuilder();

        Scanner queryReader = new Scanner(inStream, StandardCharsets.UTF_8);
        while(queryReader.hasNext()) {
            queryBuilder.append(queryReader.nextLine()).append('\n');
        }
        queryReader.close();

        return queryBuilder.toString();
    }

    // Connector Type Getter
    @Nullable
    public static String getConnectorType() {
        return dbType;
    }

    // Module Checker
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isModuleLoaded() {
        return dbConnection != null;
    }

    // Methods
    public synchronized static void load(@NotNull final String jdbcConnectionString) throws ModuleException {
        load(jdbcConnectionString, false);
    }
    public static void load(@NotNull String jdbcConnectionString, final boolean disableLog) throws ModuleException {

        if (jdbcConnectionString.equalsIgnoreCase(Defs.DBConnection.JDBC_DB_UNCONFIGURED)) {
            if (!disableLog) ServerLogger.getLogger().warning("DBConnection Module Load: Canceled! (Reason: the jdbc connection string in the config wasn't configured)");
            throw new ModuleMissingConfigsException("DBConnection Module Load: Canceled! (Reason: the jdbc connection string in the config wasn't configured)");
        }

        if (isDBConnecting) {
            if (!disableLog) ServerLogger.getLogger().warning("DBConnection Module Load: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
            throw new ModuleLoadingException("DBConnection Module Load: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
        }
        if (isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("DBConnection Module Load: Failed! (Reason: the module has already been loaded)");
            throw new ModuleAlreadyLoadedException("DBConnection Module Load: Failed! (Reason: the module has already been loaded)");
        }

        isDBConnecting = true;

        // DB Type Assigner
        if (jdbcConnectionString.startsWith(Defs.DBConnection.JDBC_MYSQL_CONNECTOR_STRING_START)) {
            dbType = Defs.DBConnection.MYSQL_CONNECTOR;
        }else if (jdbcConnectionString.startsWith(Defs.DBConnection.JDBC_SQLITE_CONNECTOR_STRING_START)) {
            dbType = Defs.DBConnection.SQLITE_CONNECTOR;
        }else if (jdbcConnectionString.startsWith(Defs.DBConnection.JDBC_POSTGRESQL_CONNECTOR_STRING_START)) {
            dbType = Defs.DBConnection.POSTGRESQL_CONNECTOR;
        }else {
            throw new ModuleLoadingException("DBConnection Module Load: Failed! (Reason: can't recognize connector type)");
        }

        if (jdbcConnectionString.contains("allowMultiQueries=false")) {
            jdbcConnectionString = jdbcConnectionString.replace("?allowMultiQueries=false", "allowMultiQueries=true");
        }else if (!jdbcConnectionString.contains("allowMultiQueries=true")) {
            if (!jdbcConnectionString.contains("?")) {
                jdbcConnectionString += '?';
            }else {
                jdbcConnectionString += '&';
            }
            jdbcConnectionString+="allowMultiQueries=true";
        }

        try {
            dbConnection = DriverManager.getConnection(jdbcConnectionString);
            dbConnection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                if (dbConnection != null) dbConnection.close();
            } catch (Exception ignored){}
            dbType = null;
            dbConnection = null;
            isDBConnecting = false;
            if (!disableLog) ServerLogger.getLogger().severe("DBConnection Module Load: Failed! (Reason: an error during connection has occurred)");
            throw new ModuleLoadingException("DBConnection Module Load: Failed! (Reason: an error during connection has occurred)", e);
        }

        isDBConnecting = false;
        if (!disableLog) ServerLogger.getLogger().info("DBConnection Module Load: Successful!");
    }
    public static void unload() throws ModuleException {
        unload(true);
    }
    public static void unload(final boolean disableLog) throws ModuleException {

        if (isDBConnecting) {
            if (!disableLog) ServerLogger.getLogger().warning("DBConnection Module Unload: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
            throw new ModuleLoadingException("DBConnection Module Unload: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
        }
        if (!isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("DBConnection Module Unload: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("DBConnection Module Unload: Failed! (Reason: the module isn't loaded)");
        }

        try {
            if(dbConnection != null) dbConnection.close();
        }catch (SQLException ignored){}

        dbType = null;
        dbConnection = null;

        if (!disableLog) ServerLogger.getLogger().info("DBConnection Module Unload: Successful!");
    }
    public static void reload(@NotNull final String jdbcConnectionString) throws ModuleException {
        reload(jdbcConnectionString, false);
    }
    public static void reload(@NotNull final String jdbcConnectionString, final boolean disableLog) throws ModuleException {

        if (isDBConnecting) {
            if (!disableLog) ServerLogger.getLogger().warning("DBConnection Module Reload: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
            throw new ModuleLoadingException("DBConnection Module Reload: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
        }
        if (isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("DBConnection Module Reload: Failed! (Reason: the module has already been loaded)");
            throw new ModuleAlreadyLoadedException("DBConnection Module Reload: Failed! (Reason: the module has already been loaded)");
        }

        try {
            unload(true);
        } catch (ModuleException e) {
            if (!disableLog) ServerLogger.getLogger().severe("DBConnection Module Reload: Failed! (Reason: an error has occurred in the unload routine)");
            throw new ModuleReloadingException("DBConnection Module Reload: Failed! (Reason: an error has occurred in the unload routine)");
        }

        try {
            load(jdbcConnectionString, true);
        } catch (ModuleException e) {
            if (!disableLog) ServerLogger.getLogger().severe("DBConnection Module Reload: Failed! (Reason: the load routine has failed)");
            throw new ModuleReloadingException("DBConnection Module Reload: Failed! (Reason: the load routine has failed)", e);
        }

        if (!disableLog) ServerLogger.getLogger().info("DBConnection Module Reload: Successful!");
    }
    @NotNull
    public static PreparedStatement getPreparedStatement(@NotNull final String sql) throws ModuleException {

        if (isDBConnecting) {
            ServerLogger.getLogger().warning("GetPreparedStatement Operation: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
            throw new ModuleLoadingException("GetPreparedStatement Operation: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
        }
        if (!isModuleLoaded()) {
            ServerLogger.getLogger().severe("GetPreparedStatement Operation: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("GetPreparedStatement Operation: Failed! (Reason: the module isn't loaded)");
        }

        try {
            return dbConnection.prepareStatement(sql);
        } catch (SQLException e) {
            ServerLogger.getLogger().severe("GetPreparedStatement Operation: Failed! (Reason: an error has occurred with the sql query)");
            throw new ModuleOperationException("GetPreparedStatement Operation: Failed! (Reason: an error has occurred with the sql query)", e);
        }
    }
    @Nullable @SuppressWarnings("UnusedReturnValue")
    public static ResultSet executeStatementFromQuery(@NotNull final String query) throws ModuleException {

        if (isDBConnecting) {
            ServerLogger.getLogger().warning("ExecuteStatementFromQuery Operation: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
            throw new ModuleLoadingException("ExecuteStatementFromQuery Operation: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
        }
        if (!isModuleLoaded()) {
            ServerLogger.getLogger().severe("ExecuteStatementFromQuery Operation: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("ExecuteStatementFromQuery Operation: Failed! (Reason: the module isn't loaded)");
        }

        Statement statement;
        try {
            statement = dbConnection.createStatement();
        } catch (SQLException e) {
            ServerLogger.getLogger().severe("ExecuteStatementFromQuery Operation: Failed! (Reason: an error has occurred with the sql query)");
            throw new ModuleOperationException("ExecuteStatementFromQuery Operation: Failed! (Reason: an error has occurred with the sql query)", e);
        }

        try {
            statement.execute(query);
            ResultSet resultSet = statement.getResultSet();
            statement.close();
            return resultSet;
        } catch (SQLException e) {
            try {
                statement.close();
            } catch (Exception ignored) {}
            ServerLogger.getLogger().severe("ExecuteStatementFromQuery Operation: Failed! (Reason: an error has occurred with the sql query)");
            throw new ModuleOperationException("ExecuteStatementFromQuery Operation: Failed! (Reason: an error has occurred with the sql query)", e);
        }
    }
    public static void executeStatementFromQueryIgnoreResult(@NotNull final String query) throws ModuleException {
        ResultSet resultSet = executeStatementFromQuery(query);
        try {
            if (resultSet != null) resultSet.close();
        }catch (SQLException ignored) {}
    }
    @Nullable @SuppressWarnings("UnusedReturnValue")
    public static ResultSet executePreparedStatement(@NotNull final PreparedStatement preparedStatement, boolean closeStatementAfterExecute) throws ModuleException {

        if (isDBConnecting) {
            ServerLogger.getLogger().warning("ExecutePreparedStatement Operation: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
            throw new ModuleLoadingException("ExecutePreparedStatement Operation: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
        }
        if (!isModuleLoaded()) {
            ServerLogger.getLogger().severe("ExecutePreparedStatement Operation: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("ExecutePreparedStatement Operation: Failed! (Reason: the module isn't loaded)");
        }

        try {
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            if (closeStatementAfterExecute) preparedStatement.close();
            return resultSet;
        } catch (SQLException e) {
            try {
                preparedStatement.close();
            } catch (Exception ignored) {}
            ServerLogger.getLogger().severe("ExecutePreparedStatement Operation: Failed! (Reason: an error has occurred with the sql query)");
            throw new ModuleOperationException("ExecutePreparedStatement Operation: Failed! (Reason: an error has occurred with the sql query)", e);
        }
    }
    @Nullable @SuppressWarnings("UnusedReturnValue")
    public static ResultSet executePreparedStatementFromQuery(@NotNull final String query) throws ModuleException {

        if (isDBConnecting) {
            ServerLogger.getLogger().warning("ExecuteStatementFromQuery Operation: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
            throw new ModuleLoadingException("ExecuteStatementFromQuery Operation: Canceled! (Reason: Another thread is executing a dbconnection loading command)");
        }
        if (!isModuleLoaded()) {
            ServerLogger.getLogger().severe("ExecuteStatementFromQuery Operation: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("ExecuteStatementFromQuery Operation: Failed! (Reason: the module isn't loaded)");
        }

        PreparedStatement preparedStatement;
        try {
            preparedStatement = dbConnection.prepareStatement(query);
        } catch (SQLException e) {
            ServerLogger.getLogger().severe("ExecuteStatementFromQuery Operation: Failed! (Reason: an error has occurred with the sql query)");
            throw new ModuleOperationException("ExecuteStatementFromQuery Operation: Failed! (Reason: an error has occurred with the sql query)", e);
        }

        try {
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            preparedStatement.close();
            return resultSet;
        } catch (SQLException e) {
            try {
                preparedStatement.close();
            } catch (Exception ignored) {}
            ServerLogger.getLogger().severe("ExecuteStatementFromQuery Operation: Failed! (Reason: an error has occurred with the sql query)");
            throw new ModuleOperationException("ExecuteStatementFromQuery Operation: Failed! (Reason: an error has occurred with the sql query)", e);
        }
    }
    public static void executePreparedStatementFromQueryIgnoreResult(@NotNull final String query) throws ModuleException {
        ResultSet resultSet = executePreparedStatementFromQuery(query);
        try {
            if (resultSet != null) resultSet.close();
        }catch (SQLException ignored){}
    }
}

package it.italiandudes.mymcserver.modules;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.exceptions.modules.*;
import it.italiandudes.mymcserver.modules.httphandlers.CommandHTTPHandler;
import it.italiandudes.mymcserver.modules.httphandlers.LoginHTTPHandler;
import it.italiandudes.mymcserver.modules.httphandlers.StatsHTTPHandler;
import it.italiandudes.mymcserver.modules.httphandlers.remote.RemoteUser;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.Defs.Connection.JSONContent;
import it.italiandudes.mymcserver.utils.Defs.Connection.ReturnCode;
import it.italiandudes.mymcserver.utils.Defs.Localization.Keys;
import it.italiandudes.mymcserver.utils.ServerLogger;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@SuppressWarnings("unused")
public final class ConnectionModule {

    // Attributes
    private static HttpServer httpServer = null;
    private static boolean isModuleLoaded = false;
    private static boolean areConnectionLoading = false;

    // Default Constructor
    public ConnectionModule() {
        throw new RuntimeException("Can't instantiate this class!");
    }

    // Module Checker
    public static boolean isModuleLoaded() {
        return isModuleLoaded && httpServer != null;
    }

    // Methods
    public synchronized static void load(final int port) throws ModuleException {
        load(port, false);
    }
    public synchronized static void load(final int port, final boolean disableLog) throws ModuleException {

        if (!DBConnectionModule.isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().warning("Connection Module Load: Canceled! (Reason: DBConnection module isn't loaded)");
            throw new ModuleMissingDependenciesException("Connection Module Load: Canceled! (Reason: DBConnection module isn't loaded)");
        }

        if (areConnectionLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Connection Module Load: Canceled! (Reason: Another thread is executing a connection loading command)");
            throw new ModuleLoadingException("Connection Module Load: Canceled! (Reason: Another thread is executing a connection loading command)");
        }
        if (isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Load: Failed! (Reason: the module has already been loaded)");
            throw new ModuleAlreadyLoadedException("Connection Module Load: Failed! (Reason: the module has already been loaded)");
        }

        areConnectionLoading = true;

        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), Defs.Connection.BACKLOG);
            // Setting Handlers
            httpServer.createContext(CommandHTTPHandler.CONTEXT, new CommandHTTPHandler());
            httpServer.createContext(StatsHTTPHandler.CONTEXT, new StatsHTTPHandler());
            httpServer.createContext(LoginHTTPHandler.CONTEXT, new LoginHTTPHandler());

            // Starting up the server listener
            httpServer.start();
        } catch (BindException bindException) {
            areConnectionLoading = false;
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Load: Failed (Reason: the provided port for listen is already used somewhere else");
            throw new ModuleLoadingException("Connection Module Load: Failed (Reason: the provided port for listen is already used somewhere else", bindException);
        } catch (IOException e) {
            areConnectionLoading = false;
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Load: Failed! (Reason: server opening error)");
            throw new ModuleLoadingException("Connection Module Load: Failed! (Reason: server opening error)", e);
        }

        areConnectionLoading = false;
        isModuleLoaded = true;
        if (!disableLog) ServerLogger.getLogger().info("Connection Module Load: Successful!");
    }
    public synchronized static void unload() throws ModuleException {
        unload(false);
    }
    public synchronized static void unload(final boolean disableLog) throws ModuleException {

        if (areConnectionLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Connection Module Unload: Canceled! (Reason: Another thread is executing a connection loading command)");
            throw new ModuleLoadingException("Connection Module Unload: Canceled! (Reason: Another thread is executing a connection loading command)");
        }
        if (!isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Unload: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("Connection Module Unload: Failed! (Reason: the module isn't loaded)");
        }

        try {
            httpServer.stop(Defs.Connection.STOP_DELAY_SECONDS);
        }catch (Exception ignored) {}

        httpServer = null;

        if(!disableLog) ServerLogger.getLogger().info("Connection Module Unload: Successful!");
    }
    public synchronized static void reload(final int port) throws ModuleException {
        reload(port, false);
    }
    public synchronized static void reload(final int port, final boolean disableLog) throws ModuleException {

        if (areConnectionLoading) {
            if (!disableLog) ServerLogger.getLogger().warning("Connection Module Reload: Canceled! (Reason: Another thread is executing a connection loading command)");
            throw new ModuleLoadingException("Connection Module Reload: Canceled! (Reason: Another thread is executing a connection loading command)");
        }
        if (!isModuleLoaded()) {
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Reload: Failed! (Reason: the module isn't loaded)");
            throw new ModuleNotLoadedException("Connection Module Reload: Failed! (Reason: the module isn't loaded)");
        }

        try {
            unload(true);
        } catch (ModuleException e) {
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Reload: Failed! (Reason: the unload routine has failed)");
            throw new ModuleReloadingException("Connection Module Reload: Failed! (Reason: the unload routine has failed)", e);
        }

        try {
            load(port, true);
        } catch (ModuleException e) {
            if (!disableLog) ServerLogger.getLogger().severe("Connection Module Reload: Failed! (Reason: the load routine has failed)");
            throw new ModuleReloadingException("Connection Module Reload: Failed! (Reason: the load routine has failed)", e);
        }

        if (!disableLog) ServerLogger.getLogger().info("Connection Module Reload: Successful!");
    }
    public static final class CommonResponse {
        public static void sendBadRequest(@NotNull final HttpExchange exchange) throws IOException {
            sendHTTPResponse(
                exchange,
                getLocalizedErrorJSON(
                    ReturnCode.BAD_REQUEST
                )
            );
        }
        public static void sendUnauthorized(@NotNull final HttpExchange exchange) throws IOException {
            sendHTTPResponse(
                exchange,
                getLocalizedErrorJSON(
                    ReturnCode.UNAUTHORIZED
                )
            );
        }
        public static void sendForbidden(@NotNull final HttpExchange exchange) throws IOException {
            sendHTTPResponse(
                exchange,
                getLocalizedErrorJSON(
                    ReturnCode.FORBIDDEN
                )
            );
        }
        public static void sendNotFound(@NotNull final HttpExchange exchange) throws IOException {
            sendHTTPResponse(
                exchange,
                getLocalizedErrorJSON(
                    ReturnCode.NOT_FOUND
                )
            );
        }
        public static void sendInternalServerError(@NotNull final HttpExchange exchange) throws IOException {
            sendHTTPResponse(
                exchange,
                getLocalizedErrorJSON(
                    ReturnCode.INTERNAL_SERVER_ERROR
                )
            );
        }
    }
    public static void sendHTTPResponse(@NotNull final HttpExchange exchange, @NotNull final JSONObject response) throws IOException {
        int jsonReturnCode = (int) response.get(JSONContent.RETURN_CODE);
        byte[] responseBodyBytes = response.toJSONString().getBytes(StandardCharsets.ISO_8859_1);
        exchange.sendResponseHeaders(jsonReturnCode, responseBodyBytes.length);
        exchange.getResponseBody().write(responseBodyBytes, 0, responseBodyBytes.length);
        exchange.getResponseBody().flush();
    }
    @SuppressWarnings("unchecked")
    private static JSONObject getLocalizedErrorJSON(final int returnCode) {
        JSONObject object = new JSONObject();
        object.put(JSONContent.RETURN_CODE, returnCode);
        try {
            switch (returnCode) {
                case ReturnCode.BAD_REQUEST -> object.put(JSONContent.MESSAGE, LocalizationModule.translate(Keys.CONNECTION_RESPONSE_MESSAGE_BAD_REQUEST));
                case ReturnCode.UNAUTHORIZED -> object.put(JSONContent.MESSAGE, LocalizationModule.translate(Keys.CONNECTION_RESPONSE_MESSAGE_UNAUTHORIZED));
                case ReturnCode.FORBIDDEN -> object.put(JSONContent.MESSAGE, LocalizationModule.translate(Keys.CONNECTION_RESPONSE_MESSAGE_FORBIDDEN));
                case ReturnCode.NOT_FOUND -> object.put(JSONContent.MESSAGE, LocalizationModule.translate(Keys.CONNECTION_RESPONSE_MESSAGE_NOT_FOUND));
                case ReturnCode.INTERNAL_SERVER_ERROR -> object.put(JSONContent.MESSAGE, LocalizationModule.translate(Keys.CONNECTION_RESPONSE_MESSAGE_INTERNAL_SERVER_ERROR));
            }
        } catch (ModuleException e) {
            object.put(JSONContent.RETURN_CODE, ReturnCode.INTERNAL_SERVER_ERROR);
            object.put(JSONContent.MESSAGE, "An error has occurred on server side");
        }
        return object;
    }
    public static RemoteUser generateRemoteUser(@NotNull final String username, @NotNull final String sha512password) {
        long newExpirationDateEpoch = Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli();
        Random random = new Random();
        String rawToken = Math.abs(random.nextInt()) + '@' +username + '@' + newExpirationDateEpoch + '@' + sha512password + '@' + Math.abs(random.nextInt());
        String newToken = DigestUtils.sha512Hex(rawToken);
        return new RemoteUser(username, sha512password, newToken, new Date(newExpirationDateEpoch));
    }
    public static RemoteUser getUserByToken(@NotNull final String token) {
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        try {
            String query = "SELECT user_id, username, sha512password, token_expiration_date FROM remote_users WHERE token=?";
            preparedStatement = DBConnectionModule.getPreparedStatement(query);
            preparedStatement.setString(1, token);
            result = preparedStatement.executeQuery();
            int count = 0;
            String username = null;
            String sha512password = null;
            Integer userID = null;
            Date expirationDate = null;
            while (result.next()) {
                count++;
                username = result.getString("username");
                sha512password = result.getString("sha512password");
                userID = result.getInt("user_id");
                expirationDate = result.getDate("token_expiration_date");
            }
            result.close();
            preparedStatement.close();
            if (count < 0 || count > 1 || userID == null || username == null || sha512password == null || expirationDate == null) { // How is even possible that a token can have multiple users or count be less than 0?
                throw new SQLException("How is even possible that a token can have multiple users?");
            }
            return new RemoteUser(userID, username, sha512password, token, expirationDate);
        } catch (ModuleException | SQLException e) {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (Exception ignored) {}
            try {
                if (result != null) result.close();
            } catch (Exception ignored) {}
            ServerLogger.getLogger().severe("ERROR: DATABASE SELECT FAILED!");
            return null;
        }
    }
    public static String getUserToken(@NotNull final RemoteUser remoteUser) {
        return getUserToken(remoteUser.getUsername(), remoteUser.getSha512password());
    }
    public static String getUserToken(@NotNull final String username, @NotNull final String sha512password) {
        PreparedStatement preparedStatement = null;
        ResultSet result = null;
        try {
            String query = "SELECT DISTINCT user_id, token, token_expiration_date FROM remote_users WHERE username=? AND sha512password=?;";
            preparedStatement = DBConnectionModule.getPreparedStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, sha512password);
            result = preparedStatement.executeQuery();
            if (!result.next()) {
                result.close();
                preparedStatement.close();
                return null;
            }
            int userID = result.getInt("user_id");
            String token = result.getString("token");
            Date expirationDate = result.getDate("token_expiration_date");
            result.close();
            preparedStatement.close();
            String newToken;
            Instant now = Instant.now();
            if (expirationDate.before(new Date(now.plus(1, ChronoUnit.DAYS).toEpochMilli()))) {
                long newExpirationDateEpoch = now.plus(7, ChronoUnit.DAYS).toEpochMilli();
                Random random = new Random();
                String rawToken = Math.abs(random.nextInt()) + '@' +username + '@' + newExpirationDateEpoch + '@' + sha512password + '@' + Math.abs(random.nextInt());
                newToken = DigestUtils.sha512Hex(rawToken);
                query = "UPDATE remote_users SET token=?, token_expiration_date=? WHERE user_id=?;";
                preparedStatement = DBConnectionModule.getPreparedStatement(query);
                preparedStatement.setString(1, newToken);
                preparedStatement.setDate(2, new Date(newExpirationDateEpoch));
                preparedStatement.setInt(3, userID);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                return newToken;
            }
            return token;
        } catch (ModuleException | SQLException e) {
            try {
                if (preparedStatement != null) preparedStatement.close();
            } catch (Exception ignored) {}
            try {
                if (result != null) result.close();
            } catch (Exception ignored) {}
            ServerLogger.getLogger().severe("ERROR: DATABASE UPDATE FAILED!");
            return null;
        }
    }
}

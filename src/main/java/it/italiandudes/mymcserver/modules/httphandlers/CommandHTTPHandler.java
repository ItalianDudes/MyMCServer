package it.italiandudes.mymcserver.modules.httphandlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.italiandudes.mymcserver.MyMCServer;
import it.italiandudes.mymcserver.modules.ConnectionModule;
import it.italiandudes.mymcserver.modules.commandsender.RemoteCommandSender;
import it.italiandudes.mymcserver.modules.httphandlers.remote.RemoteUser;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.Defs.Connection.Context;
import it.italiandudes.mymcserver.utils.Defs.Connection.Header;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public final class CommandHTTPHandler implements HttpHandler {

    // Context
    public static final String CONTEXT = Context.CONTEXT_COMMAND;

    // Handler
    @Override @SuppressWarnings({"unchecked", "DuplicatedCode"})
    public void handle(@NotNull final HttpExchange exchange) throws IOException {

        // Send NOT FOUND if the context was not correct
        if (!exchange.getRequestURI().toString().equals(CONTEXT)) {
            ConnectionModule.CommonResponse.sendNotFound(exchange);
        }

        // Get Headers
        Headers requestHeaders = exchange.getRequestHeaders();

        // Parsing headers
        List<String> tokenHeader = requestHeaders.get(Header.HEADER_TOKEN);
        List<String> commandHeader = requestHeaders.get(Header.HEADER_COMMAND);

        if (
            (tokenHeader == null || tokenHeader.size() != 1) ||
            (commandHeader == null || commandHeader.size() != 1)
        ) { // The request is invalid
            ConnectionModule.CommonResponse.sendBadRequest(exchange);
            exchange.close();
            return;
        }

        // Getting required data
        String token = tokenHeader.get(0);
        String command = commandHeader.get(0);

        // Getting the username from the token
        RemoteUser remoteUser = ConnectionModule.getUserByToken(token);
        if (remoteUser == null) {
            ConnectionModule.CommonResponse.sendForbidden(exchange);
            exchange.close();
            return;
        }

        // Creating instance of RemoteCommandSender
        RemoteCommandSender remoteCommandSender = new RemoteCommandSender(Bukkit.getConsoleSender());

        // Sending the command with a fake instance of the player
        AtomicBoolean finish = new AtomicBoolean(false);
        Runnable runnable = () -> {
            Bukkit.dispatchCommand(remoteCommandSender, command);
            finish.set(true);
        };
        Bukkit.getScheduler().runTask(MyMCServer.getPluginInstance(), runnable);

        //noinspection StatementWithEmptyBody
        while (!finish.get());

        String commandOutput = remoteCommandSender.getChatOutput();

        // Send the response
        JSONObject response = new JSONObject();
        response.put(Defs.Connection.JSONContent.RETURN_CODE, Defs.Connection.ReturnCode.OK);
        response.put(Defs.Connection.JSONContent.COMMAND_OUTPUT, commandOutput);
        ConnectionModule.sendHTTPResponse(exchange, response);

        // Finishing the transaction
        exchange.close();
    }
}

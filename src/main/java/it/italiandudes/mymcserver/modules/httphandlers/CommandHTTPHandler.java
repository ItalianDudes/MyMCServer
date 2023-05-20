package it.italiandudes.mymcserver.modules.httphandlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.italiandudes.mymcserver.MyMCServer;
import it.italiandudes.mymcserver.modules.CommandsModule.PlayerCommandSender;
import it.italiandudes.mymcserver.modules.ConnectionModule;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.Defs.Connection.Context;
import it.italiandudes.mymcserver.utils.Defs.Connection.Header;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

@SuppressWarnings("unused")
public final class CommandHTTPHandler implements HttpHandler {

    // Context
    public static final String CONTEXT = Context.CONTEXT_COMMAND;

    // Handler
    @Override @SuppressWarnings({"unchecked", "DuplicatedCode"})
    public void handle(@NotNull final HttpExchange exchange) throws IOException {

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
        String username = ConnectionModule.getUserByToken(token);
        if (username == null) {
            ConnectionModule.CommonResponse.sendInternalServerError(exchange);
            exchange.close();
            return;
        }

        // Getting player from username
        Player player = MyMCServer.getPluginInstance().getServer().getPlayer(username);
        if (player == null) { // How it's possible that the user is null?
            ConnectionModule.CommonResponse.sendInternalServerError(exchange);
            exchange.close();
            return;
        }

        // Sending the command with a fake instance of the player
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outStream);
        PlayerCommandSender commandSender = new PlayerCommandSender(player, printStream);
        MyMCServer.getServerInstance().dispatchCommand(commandSender, command);
        String commandOutput = outStream.toString();
        try {
            printStream.close();
        }catch (Exception ignored) {}
        try {
            outStream.close();
        }catch (Exception ignored) {}

        // Send the response
        JSONObject response = new JSONObject();
        response.put(Defs.Connection.JSONContent.RETURN_CODE, Defs.Connection.ReturnCode.OK);
        response.put(Defs.Connection.JSONContent.COMMAND_OUTPUT, commandOutput);
        ConnectionModule.sendHTTPResponse(exchange, response);

        // Finishing the transaction
        exchange.close();
    }
}

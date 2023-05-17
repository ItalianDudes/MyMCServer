package it.italiandudes.mymcserver.modules.httphandlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.italiandudes.mymcserver.modules.ConnectionModule;
import it.italiandudes.mymcserver.utils.Defs.Connection.Context;
import it.italiandudes.mymcserver.utils.Defs.Connection.ReturnCode;
import it.italiandudes.mymcserver.modules.ConnectionModule.CommonResponse;
import it.italiandudes.mymcserver.utils.Defs.Connection.Header;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@SuppressWarnings("unused")
public final class CommandHTTPHandler implements HttpHandler {

    // Context
    public static final String CONTEXT = Context.CONTEXT_COMMAND;

    // Handler
    @Override
    public void handle(@NotNull final HttpExchange exchange) throws IOException {

        // Get Headers
        Headers requestHeaders = exchange.getRequestHeaders();

        // Getting headers
        List<String> tokenHeader = requestHeaders.get(Header.HEADER_TOKEN);
        List<String> commandHeader = requestHeaders.get(Header.HEADER_COMMAND);

        if (
                (tokenHeader == null || tokenHeader.size() != 1) ||
                (commandHeader == null || commandHeader.size() != 1)
        ) {
            CommonResponse.sendBadRequest(exchange);
            exchange.close();
            return;
        }

        String token = tokenHeader.get(0);
        String command = commandHeader.get(0);

        // TODO: finish implementation of CommandHTTPHandler
    }
}

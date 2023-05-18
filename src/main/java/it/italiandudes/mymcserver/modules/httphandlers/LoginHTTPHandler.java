package it.italiandudes.mymcserver.modules.httphandlers;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.italiandudes.mymcserver.modules.LocalizationModule;
import it.italiandudes.mymcserver.utils.Defs.Connection.JSONContent;
import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.modules.ConnectionModule;
import it.italiandudes.mymcserver.utils.Defs;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
public class LoginHTTPHandler implements HttpHandler {

    // Context
    public static final String CONTEXT = Defs.Connection.Context.CONTEXT_LOGIN;

    // Handler
    @Override @SuppressWarnings({"unchecked", "DuplicatedCode"})
    public void handle(@NotNull final HttpExchange exchange) throws IOException {

        // Get Headers
        Headers requestHeaders = exchange.getRequestHeaders();

        // Parsing headers
        List<String> usernameHeader = requestHeaders.get(Defs.Connection.Header.HEADER_USERNAME);
        List<String> passwordHeader = requestHeaders.get(Defs.Connection.Header.HEADER_PASSWORD);

        if (
            (usernameHeader == null || usernameHeader.size() != 1) ||
            (passwordHeader == null || passwordHeader.size() != 1)
        ) { // The request is invalid
            ConnectionModule.CommonResponse.sendBadRequest(exchange);
            exchange.close();
            return;
        }

        // Getting required data
        String username = usernameHeader.get(0);
        String sha512password = passwordHeader.get(0);

        // Getting the token if the authentication succeed
        String token = ConnectionModule.getUserToken(username, sha512password);

        if (token == null) { // The authentication failed
            ConnectionModule.CommonResponse.sendForbidden(exchange);
            exchange.close();
            return;
        }

        // Send the response with the token
        try {
            JSONObject response = new JSONObject();
            response.put(JSONContent.RETURN_CODE, Defs.Connection.ReturnCode.OK);
            response.put(JSONContent.MESSAGE, LocalizationModule.translate(Defs.Localization.Keys.CONNECTION_RESPONSE_MESSAGE_LOGIN_SUCCESS));
            response.put(JSONContent.TOKEN, token);
            ConnectionModule.sendHTTPResponse(exchange, response);
        } catch (ModuleException e) {
            ConnectionModule.CommonResponse.sendInternalServerError(exchange);
        }

        // Finishing the transaction
        exchange.close();
    }
}

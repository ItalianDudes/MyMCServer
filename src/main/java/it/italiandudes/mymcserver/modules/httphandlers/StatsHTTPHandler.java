package it.italiandudes.mymcserver.modules.httphandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.italiandudes.mymcserver.utils.Defs;

import java.io.IOException;

@SuppressWarnings("unused")
public final class StatsHTTPHandler implements HttpHandler {

    // Context
    public static final String CONTEXT = Defs.Connection.Context.CONTEXT_STATS;

    // Handler
    @Override @SuppressWarnings({"unchecked", "DuplicatedCode"})
    public void handle(HttpExchange exchange) throws IOException {

    }
}

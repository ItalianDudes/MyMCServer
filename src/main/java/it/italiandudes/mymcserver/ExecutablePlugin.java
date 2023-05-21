package it.italiandudes.mymcserver;

import it.italiandudes.mymcserver.utils.Defs;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public final class ExecutablePlugin {

    // Main Method
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        System.out.print("Insert server address (indirizzo:porta): ");
        String serverAddress = scan.nextLine();
        System.out.print("Insert username: ");
        String username = scan.nextLine();
        System.out.print("Insert password: ");
        String sha512password = DigestUtils.sha512Hex(scan.nextLine());

        // Create the destination point
        HttpRequest request;
        HttpResponse<String> response;
        JSONParser parser = new JSONParser();
        JSONObject responseBody;
        HttpClient client = HttpClient.newHttpClient();
        String token = null;

        // Compiling Login Header
        try {
            request = HttpRequest.newBuilder().GET().
                    setHeader(Defs.Connection.Header.HEADER_USERNAME, username).
                    setHeader(Defs.Connection.Header.HEADER_PASSWORD, sha512password).
                    uri(new URI("http://" + serverAddress + Defs.Connection.Context.CONTEXT_LOGIN)).
                    build();
            response = client.send(request, (HttpResponse.BodyHandler<String>) HttpRequest.BodyPublishers.noBody());
            responseBody = (JSONObject) parser.parse(response.body());
            if (((int) responseBody.get(Defs.Connection.JSONContent.RETURN_CODE)) == 200) {
                token = (String) responseBody.get(Defs.Connection.JSONContent.TOKEN);
            }else {
                System.err.println("Error: username or password are wrong");
            }
        } catch (Exception e) {
            System.err.println("An error has occurred attempting the connection");
            System.exit(0);
        }

        System.out.println("Connection established");
        System.out.println("Digit \"terminate_remote_session\" to quit");
        System.out.println("Digit \"stats_remote_session\" to see server info");

        String input;
        try {
            while (true) {
                input = scan.nextLine();
                if (input.equals("termite_remote_session")) {
                    break;
                }
                if (input.equals("stats_remote_session")) {
                    request = HttpRequest.newBuilder().GET().
                            setHeader(Defs.Connection.Header.HEADER_TOKEN, token).
                            uri(new URI("http://" + serverAddress + Defs.Connection.Context.CONTEXT_STATS)).
                            build();
                    response = client.send(request, (HttpResponse.BodyHandler<String>) HttpRequest.BodyPublishers.noBody());
                    System.out.println(new String(response.body().getBytes(), StandardCharsets.UTF_8));
                }else {
                    request = HttpRequest.newBuilder().GET().
                            setHeader(Defs.Connection.Header.HEADER_TOKEN, token).
                            setHeader(Defs.Connection.Header.HEADER_COMMAND, input).
                            uri(new URI("http://" + serverAddress + Defs.Connection.Context.CONTEXT_COMMAND)).
                            build();
                    response = client.send(request, (HttpResponse.BodyHandler<String>) HttpRequest.BodyPublishers.noBody());
                    responseBody = (JSONObject) parser.parse(new String(response.body().getBytes(), StandardCharsets.UTF_8));
                    System.out.println(responseBody.get(Defs.Connection.JSONContent.COMMAND_OUTPUT));
                }
            }
        } catch (Exception e) {
            System.err.println("An error has occurred, this session is terminated");
        }
    }
}

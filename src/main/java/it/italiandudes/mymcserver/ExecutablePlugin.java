package it.italiandudes.mymcserver;

import it.italiandudes.idl.common.Credential;
import it.italiandudes.idl.common.Peer;
import it.italiandudes.idl.common.PeerSerializer;
import it.italiandudes.idl.common.RawSerializer;
import it.italiandudes.mymcserver.utils.Defs;

import java.net.ProtocolException;
import java.net.Socket;
import java.util.Scanner;

public final class ExecutablePlugin {

    // Main Method
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        System.out.print("Inserire indirizzo server (indirizzo:porta): ");
        String serverAddress = scan.nextLine();
        String[] splitAddress = serverAddress.split(":");
        System.out.print("Inserire nome utente: ");
        String username = scan.nextLine();
        System.out.print("Inserire password: ");
        String password = scan.nextLine();
        Credential credentials = new Credential(username, password);

        Socket socket = null;
        String buffer;
        String outputCommand, response;
        try {
            socket = new Socket(splitAddress[0], Integer.parseInt(splitAddress[1]));
            RawSerializer.sendString(socket.getOutputStream(), credentials.getUsername());
            RawSerializer.sendString(socket.getOutputStream(), credentials.getPassword());

            if (!RawSerializer.receiveBoolean(socket.getInputStream())) {
                System.err.println("Login error");
                throw new ProtocolException("Login error");
            }

            Peer peer = new Peer(socket, credentials);

            while (true) {
                buffer = scan.nextLine();
                if (buffer.equalsIgnoreCase("exit")) {
                    PeerSerializer.sendString(peer, Defs.Protocol.PROTOCOL_DISCONNECT);
                    break;
                }
                outputCommand = Defs.Protocol.PROTOCOL_COMMAND+" \""+buffer+"\"";
                PeerSerializer.sendString(peer, outputCommand);
                response = PeerSerializer.receiveString(peer);
                System.out.println(response);
            }
        } catch (Exception e) {
            try {
                if (socket != null) socket.close();
            }catch (Exception ignored) {}
        }

    }
}

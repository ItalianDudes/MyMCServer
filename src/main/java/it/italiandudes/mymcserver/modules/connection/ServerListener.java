package it.italiandudes.mymcserver.modules.connection;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("unused")
public final class ServerListener extends Thread {

    // Attributes
    private ServerSocket serverSocket;

    // Constructor
    public ServerListener(@NotNull final ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.setDaemon(true);
        this.setName("MyMCServerListener");
    }

    // Thread Killer
    public void closeListener() {
        try {
            serverSocket.close();
        }catch (Exception ignored) {}
        serverSocket = null;
    }

    // Thread Method
    @Override
    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    Socket newConnection = serverSocket.accept();
                    // TODO: handle the connection
                    newConnection.close(); // TODO: Workaorund: va rimosso dopo l'implementazione di un handler
                }catch (IOException ignored){}
            }
        }catch (NullPointerException ignored){}
    }
}
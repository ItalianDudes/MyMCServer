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

    public void closeListener() {
        try {
            serverSocket.close();
        }catch (Exception ignored) {}
        serverSocket = null;
    }

    @Override
    public void run() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    Socket newConnection = serverSocket.accept();
                    // TODO: handle the connection
                }catch (IOException ignored){}
            }
        }catch (NullPointerException ignored){}
    }
}
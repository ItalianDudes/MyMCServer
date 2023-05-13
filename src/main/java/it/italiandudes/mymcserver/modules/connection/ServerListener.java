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
                Socket newConnection;
                try {
                    newConnection = serverSocket.accept();
                }catch (IOException e) {
                    newConnection = null;
                }
                if (newConnection != null) {
                    new LoginHandler(newConnection).start();
                }
            }
        }catch (NullPointerException ignored){}
    }
}
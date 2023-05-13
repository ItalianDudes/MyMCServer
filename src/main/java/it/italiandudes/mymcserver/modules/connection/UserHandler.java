package it.italiandudes.mymcserver.modules.connection;

import it.italiandudes.idl.common.Peer;
import it.italiandudes.idl.common.StringHandler;
import it.italiandudes.mymcserver.utils.Defs;
import org.jetbrains.annotations.NotNull;

public class UserHandler extends Thread {

    // Attributes
    private final Peer peer;
    private final ProtocolExecutionQueue queue;

    // Constructors
    public UserHandler(@NotNull final Peer peer) {
        this.peer = peer;
        this.setDaemon(true);
        this.setName("UserHandler: "+peer.getCredential().getUsername());
        queue = new ProtocolExecutionQueue();
    }

    // Thread Killer
    public void closeUserHandler() {
        queue.shutdown();
    }

    // Methods
    @NotNull
    public Peer getPeer() {
        return peer;
    }

    // Protocol Handler
    private boolean handleProtocol(String protocol) {
        if (protocol == null) return false;

        String[] splitProtocol = StringHandler.parseString(protocol);

        switch (splitProtocol[0]) {

            // TODO: Implement some protocol here

            case Defs.Protocol.PROTOCOL_DISCONNECT -> {
                return false;
            }
        }

        return true;
    }

    // Thread Method
    @Override
    public void run() {
        while (!queue.isShutdown()) {
            handleProtocol(queue.dequeue());
            if (queue.isEmpty()) {
                Thread.currentThread().wait();
            }
        }
        //noinspection StatementWithEmptyBody
        while (handleProtocol(queue.dequeue()));
        try {
            peer.getPeerSocket().close();
        }catch (Exception ignored) {}
    }
}

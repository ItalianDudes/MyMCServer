package it.italiandudes.mymcserver.modules.connection;

import it.italiandudes.idl.common.Peer;
import it.italiandudes.idl.common.PeerSerializer;
import it.italiandudes.idl.common.StringHandler;
import it.italiandudes.mymcserver.MyMCServer;
import it.italiandudes.mymcserver.utils.Defs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ProtocolException;

public class UserHandler extends Thread {

    // Attributes
    private final Peer peer;
    private final ProtocolExecutionQueue queue;

    // Constructors
    public UserHandler(@NotNull final Peer peer) {
        this.peer = peer;
        this.setDaemon(true);
        this.setName("UserHandler: "+peer.getCredential().getUsername());
        queue = new ProtocolExecutionQueue(this);
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
    private boolean handleProtocol(String protocol) throws IOException {
        if (protocol == null) return false;

        String[] splitProtocol = StringHandler.parseString(protocol);

        switch (splitProtocol[0]) {

            case Defs.Protocol.PROTOCOL_COMMAND -> {

                if (splitProtocol.length < 2) {
                    throw new ProtocolException("Protocol error");
                }

                Player player = MyMCServer.getPluginInstance().getServer().getPlayer(peer.getCredential().getUsername());
                if (player == null) {
                    throw new ProtocolException("This user doesn't exist");
                }

                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(outStream);
                PlayerCommandSender commandSender = new PlayerCommandSender(player, printStream);
                MyMCServer.getServerInstance().dispatchCommand(commandSender, splitProtocol[1]);
                PeerSerializer.sendString(peer, outStream.toString()); // Send command output to the REAL sender
                try {
                    outStream.close();
                }catch (Exception ignored) {}
                try {
                    printStream.close();
                }catch (Exception ignored) {}
            }

            case Defs.Protocol.PROTOCOL_DISCONNECT -> throw new RuntimeException();

            default -> throw new ProtocolException("Protocol not respected");
        }

        return true;
    }

    // Thread Method
    @Override
    public void run() {
        try {
            while (!queue.isShutdown()) {
                handleProtocol(queue.dequeue());
                if (queue.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {}
                }
            }
            //noinspection StatementWithEmptyBody
            while (handleProtocol(queue.dequeue())) ;
        } catch (IOException | RuntimeException ignored) {}
        queue.shutdown();
        queue.clear();
        try {
            peer.getPeerSocket().close();
        }catch (Exception ignored) {}
    }
}

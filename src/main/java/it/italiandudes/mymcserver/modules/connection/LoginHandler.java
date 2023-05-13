package it.italiandudes.mymcserver.modules.connection;

import it.italiandudes.idl.common.Credential;
import it.italiandudes.idl.common.Peer;
import it.italiandudes.idl.common.RawSerializer;
import it.italiandudes.mymcserver.modules.DBConnectionModule;
import it.italiandudes.mymcserver.utils.Defs.Protocol.StartingProtocol;
import org.jetbrains.annotations.NotNull;

import java.net.ProtocolException;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class LoginHandler extends Thread {

    // Attributes
    private final Socket socket;

    // Constructors
    public LoginHandler(@NotNull final Socket socket) {
        this.socket = socket;
        this.setDaemon(true);
    }

    // Thread Method
    @Override
    public void run() {
        try {

            String startingProtocol = RawSerializer.receiveString(socket.getInputStream());
            //noinspection SwitchStatementWithTooFewBranches
            switch (startingProtocol) {

                case StartingProtocol.STARTING_PROTOCOL_LOGIN -> {
                    String username = RawSerializer.receiveString(socket.getInputStream());
                    String password = RawSerializer.receiveString(socket.getInputStream());

                    PreparedStatement ps = DBConnectionModule.getPreparedStatement("SELECT * FROM remote_users WHERE name=? AND password=?;");
                    ps.setString(1, username);
                    ps.setString(2, password);

                    ResultSet result = ps.executeQuery();

                    int numResults = 0;
                    while (result.next()) {
                        numResults++;
                    }

                    try {
                        result.close();
                    } catch (SQLException ignored){}
                    try {
                        ps.close();
                    } catch (SQLException ignored){}

                    if (numResults != 1) {
                        RawSerializer.sendBoolean(socket.getOutputStream(), false);
                        throw new ProtocolException("Login error");
                    }

                    Credential userCredential = new Credential(username, password, false);
                    Peer userPeer = new Peer(socket, userCredential);
                    if (!ConnectedUserSet.addUserHandler(userPeer)) {
                        RawSerializer.sendBoolean(socket.getOutputStream(), false);
                        throw new ProtocolException("Already logged in");
                    }
                    RawSerializer.sendBoolean(socket.getOutputStream(), true);
                }

                default -> throw new ProtocolException("Protocol not respected");

            }
        }catch (Exception e) {
            try {
                socket.close();
            }catch (Exception ignored){}
        }
    }
}

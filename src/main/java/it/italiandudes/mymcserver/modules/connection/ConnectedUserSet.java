package it.italiandudes.mymcserver.modules.connection;

import it.italiandudes.idl.common.Credential;
import it.italiandudes.idl.common.Peer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

@SuppressWarnings("unused")
public final class ConnectedUserSet {

    // Attributes
    private final static HashSet<UserHandler> userHandlerList = new HashSet<>();

    // Methods
    public static boolean addUserHandler(@NotNull final UserHandler newUserHandler) {
        return userHandlerList.add(newUserHandler);
    }
    public static boolean addUserHandler(@NotNull final Peer peer) {
        if (containsUserHandler(peer)) return false;
        UserHandler newUserHandler = new UserHandler(peer);
        newUserHandler.start();
        return userHandlerList.add(newUserHandler);
    }
    public static boolean removeUserHandler(@NotNull final UserHandler userHandler) {
        if (!userHandlerList.contains(userHandler)) return false;
        userHandler.closeUserHandler();
        return userHandlerList.remove(userHandler);
    }
    public static boolean removeUserHandler(@NotNull final Peer peer) {
        for (UserHandler userHandler : userHandlerList) {
            if (userHandler.getPeer().equals(peer)) {
                return removeUserHandler(userHandler);
            }
        }
        return false;
    }
    public static boolean removeUserHandler(@NotNull final Credential credential) {
        for (UserHandler userHandler : userHandlerList) {
            if (userHandler.getPeer().getCredential().equals(credential)) {
                return removeUserHandler(userHandler);
            }
        }
        return false;
    }
    public static boolean removeUserHandler(@NotNull final String username) {
        for (UserHandler userHandler : userHandlerList) {
            if (userHandler.getPeer().getCredential().getUsername().equals(username)) {
                return removeUserHandler(userHandler);
            }
        }
        return false;
    }
    public static boolean containsUserHandler(@NotNull final UserHandler userHandler) {
        return userHandlerList.contains(userHandler);
    }
    public static boolean containsUserHandler(@NotNull final Peer peer) {
        for (UserHandler userHandler : userHandlerList) {
            if (userHandler.getPeer().equals(peer)) {
                return true;
            }
        }
        return false;
    }
    public static boolean containsUserHandler(@NotNull final Credential credential) {
        for (UserHandler userHandler : userHandlerList) {
            if (userHandler.getPeer().getCredential().equals(credential)) {
                return true;
            }
        }
        return false;
    }
    public static boolean containsUserHandler(@NotNull final String username) {
        for (UserHandler userHandler : userHandlerList) {
            if (userHandler.getPeer().getCredential().getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }
    public static void clear() {
        for (UserHandler userHandler : userHandlerList) {
            removeUserHandler(userHandler);
        }
        userHandlerList.clear();
    }
}

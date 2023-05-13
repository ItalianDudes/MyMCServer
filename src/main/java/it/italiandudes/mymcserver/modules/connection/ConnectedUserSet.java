package it.italiandudes.mymcserver.modules.connection;

import it.italiandudes.idl.common.Credential;
import it.italiandudes.idl.common.Peer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

@SuppressWarnings("unused")
public final class ConnectedUserSet {

    // Attributes
    private final static HashSet<Peer> peerList = new HashSet<>();

    // Methods
    private static boolean addPeer(@NotNull final Peer newPeer) {
        return peerList.add(newPeer);
    }
    private static boolean removePeer(@NotNull final Peer peer) {
        try {
            peer.getPeerSocket().close();
        }catch (Exception ignored) {}
        return peerList.remove(peer);
    }
    private static boolean removePeer(@NotNull final Credential credential) {
        for (Peer peer : peerList) {
            if (peer.getCredential().equals(credential)) {
                return removePeer(peer);
            }
        }
        return false;
    }
    private static boolean removePeer(@NotNull final String username) {
        for (Peer peer : peerList) {
            if (peer.getCredential().getUsername().equals(username)) {
                return removePeer(peer);
            }
        }
        return false;
    }
    private static boolean containsPeer(@NotNull final Peer peer) {
        return peerList.contains(peer);
    }
    private static boolean containsPeer(@NotNull final Credential credential) {
        for (Peer peer : peerList) {
            if (peer.getCredential().equals(credential)) {
                return containsPeer(peer);
            }
        }
        return false;
    }
    private static boolean containsPeer(@NotNull final String username) {
        for (Peer peer : peerList) {
            if (peer.getCredential().getUsername().equals(username)) {
                return containsPeer(peer);
            }
        }
        return false;
    }
    private static void flushSet() {
        for (Peer peer : peerList) {
            removePeer(peer);
        }
        peerList.clear();
    }
}

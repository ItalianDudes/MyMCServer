package it.italiandudes.mymcserver.modules.connection;

import it.italiandudes.mymcserver.utils.Defs;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public final class ProtocolExecutionQueue {

    // Implementing List
    @NotNull private final LinkedList<String> queue;
    private boolean isShutdown;

    // Constructors
    public ProtocolExecutionQueue() {
        this.queue = new LinkedList<>();
        isShutdown = false;
    }

    // Methods
    public synchronized void enqueue(@NotNull final String s) {
        if (isShutdown) return;
        queue.addLast(s);
    }
    public synchronized String dequeue() {
        try {
            return queue.removeFirst();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
    public synchronized boolean contains(String protocol) {
        return queue.contains(protocol);
    }
    public boolean isShutdown() {
        return isShutdown;
    }
    public synchronized void shutdown() {
        if (isShutdown) return;
        enqueue(Defs.Protocol.PROTOCOL_DISCONNECT);
        isShutdown = true;
    }
    public int size() {
        return queue.size();
    }
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    public void clear() {
        queue.clear();
    }
}

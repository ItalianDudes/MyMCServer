package it.italiandudes.mymcserver.modules.commandsender;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
public class RemoteCommandSender implements ConsoleCommandSender {

    // Attributes
    private final ConsoleCommandSender consoleCommandSender;
    private final StringBuilder chatOutput = new StringBuilder();

    // Constructors
    public RemoteCommandSender(@NotNull final ConsoleCommandSender consoleCommandSender) {
        this.consoleCommandSender = consoleCommandSender;
    }

    // Methods
    public String getChatOutput() {
        return chatOutput.toString();
    }
    @Override
    public void sendMessage(@NotNull String message) {
        chatOutput.append(message).append('\n');
    }
    @Override
    public void sendMessage(@NotNull String... messages) {
        for (String str : messages) {
            chatOutput.append(str).append('\n');
        }
    }
    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String message) {
        chatOutput.append(message).append('\n');
    }
    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String... messages) {
        for (String str : messages) {
            chatOutput.append(str).append('\n');
        }
    }
    @Override
    public @NotNull Server getServer() {
        return consoleCommandSender.getServer();
    }
    @Override
    public @NotNull String getName() {
        return consoleCommandSender.getName();
    }
    @Override
    public @NotNull Spigot spigot() {
        return consoleCommandSender.spigot();
    }
    @Override
    public @NotNull Component name() {
        return consoleCommandSender.name();
    }
    @Override
    public boolean isConversing() {
        return consoleCommandSender.isConversing();
    }
    @Override
    public void acceptConversationInput(@NotNull String input) {
        consoleCommandSender.acceptConversationInput(input);
    }
    @Override
    public boolean beginConversation(@NotNull Conversation conversation) {
        return consoleCommandSender.beginConversation(conversation);
    }
    @Override
    public void abandonConversation(@NotNull Conversation conversation) {
        consoleCommandSender.abandonConversation(conversation);
    }
    @Override
    public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent details) {
        consoleCommandSender.abandonConversation(conversation, details);
    }
    @Override
    public void sendRawMessage(@NotNull String message) {
        chatOutput.append(message).append('\n');
    }
    @Override
    public void sendRawMessage(@Nullable UUID sender, @NotNull String message) {
        chatOutput.append(message).append('\n');
    }
    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return consoleCommandSender.isPermissionSet(name);
    }
    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return consoleCommandSender.isPermissionSet(perm);
    }
    @Override
    public boolean hasPermission(@NotNull String name) {
        return consoleCommandSender.hasPermission(name);
    }
    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return consoleCommandSender.hasPermission(perm);
    }
    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return consoleCommandSender.addAttachment(plugin, name, value);
    }
    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return consoleCommandSender.addAttachment(plugin);
    }
    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return consoleCommandSender.addAttachment(plugin, name, value, ticks);
    }
    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return consoleCommandSender.addAttachment(plugin, ticks);
    }
    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        consoleCommandSender.removeAttachment(attachment);
    }
    @Override
    public void recalculatePermissions() {
        consoleCommandSender.recalculatePermissions();
    }
    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return consoleCommandSender.getEffectivePermissions();
    }
    @Override
    public boolean isOp() {
        return consoleCommandSender.isOp();
    }
    @Override
    public void setOp(boolean value) {
        consoleCommandSender.setOp(value);
    }
}

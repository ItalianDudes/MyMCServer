package it.italiandudes.mymcserver.modules.connection;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings({"deprecation", "unused"})
public class PlayerCommandSender implements CommandSender {

    // Attributes
    private final Player player;
    private final PrintStream printStream;

    // Constructors
    public PlayerCommandSender(@NotNull final Player player, @NotNull final PrintStream printStream) {
        this.player = player;
        this.printStream = printStream;
    }

    // Methods
    @Override
    public void sendMessage(@NotNull String message) {
        printStream.println(message);
    }
    @Override
    public void sendMessage(String[] messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }
    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String message) {
        player.sendMessage(sender, message);
    }
    @Override
    public void sendMessage(@Nullable UUID sender, @NotNull String... messages) {
        player.sendMessage(sender, messages);
    }
    @Override @NotNull
    public Server getServer() {
        return player.getServer();
    }
    @Override @NotNull
    public String getName() {
        return player.getName();
    }
    @Override @NotNull
    public Spigot spigot() {
        return player.spigot();
    }
    @Override
    public @NotNull Component name() {
        return player.name();
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return player.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return player.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return player.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return player.hasPermission(perm);
    }

    @Override @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return player.addAttachment(plugin, name, value);
    }

    @Override @NotNull
    public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return player.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return player.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return player.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        player.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        player.recalculatePermissions();
    }

    @Override @NotNull
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return player.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return player.isOp();
    }

    @Override
    public void setOp(boolean value) {
        player.setOp(value);
    }


}

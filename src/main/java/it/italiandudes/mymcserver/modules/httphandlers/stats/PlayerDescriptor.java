package it.italiandudes.mymcserver.modules.httphandlers.stats;

import it.italiandudes.mymcserver.utils.Defs;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;

@SuppressWarnings("unused")
public final class PlayerDescriptor {

    // Attributes
    @Nullable private final String playerName;
    private final boolean isOnline;

    // Constructors
    public PlayerDescriptor(@NotNull final OfflinePlayer player) {
        playerName = player.getName();
        isOnline = player.isOnline();
    }
    public PlayerDescriptor(@NotNull final Player player) {
        playerName = player.getName();
        isOnline = player.isOnline();
    }

    // Methods
    @Nullable
    public String getPlayerName() {
        return playerName;
    }
    public boolean isOnline() {
        return isOnline;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerDescriptor that)) return false;

        if (isOnline() != that.isOnline()) return false;
        return getPlayerName() != null ? getPlayerName().equals(that.getPlayerName()) : that.getPlayerName() == null;
    }
    @Override
    public int hashCode() {
        int result = getPlayerName() != null ? getPlayerName().hashCode() : 0;
        result = 31 * result + (isOnline() ? 1 : 0);
        return result;
    }
    @Override
    public String toString() {
        return "PlayerDescriptor{" +
                "playerName='" + playerName + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }
    @SuppressWarnings("unchecked") @Nullable
    public JSONObject getJSON() {
        if (playerName == null) return null;
        JSONObject json = new JSONObject();
        json.put(Defs.Connection.JSONContent.PlayerList.NAME, playerName);
        json.put(Defs.Connection.JSONContent.PlayerList.ONLINE, isOnline);
        return json;
    }
}

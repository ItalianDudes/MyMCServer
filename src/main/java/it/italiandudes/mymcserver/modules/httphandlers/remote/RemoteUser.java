package it.italiandudes.mymcserver.modules.httphandlers.remote;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;
import java.util.Objects;

@SuppressWarnings("unused")
public final class RemoteUser {

    // Attributes
    @Nullable private final Integer user_id;
    @NotNull private final String username;
    @NotNull private final String sha512password;
    @NotNull private final String token;
    @NotNull private final Date token_expiration_date;

    // Constructors
    public RemoteUser(@Nullable final Integer user_id, @NotNull final String username, @NotNull final String sha512password, @NotNull final String token, @NotNull final Date token_expiration_date) {
        this.user_id = user_id;
        this.username = username;
        this.sha512password = sha512password;
        this.token_expiration_date = token_expiration_date;
        this.token = token;
    }
    public RemoteUser(@NotNull final String username, @NotNull final String sha512password, @NotNull final String token, @NotNull final Date token_expiration_date) {
        this(null, username, sha512password, token, token_expiration_date);
    }

    // Methods
    @Nullable
    public Integer getUserID() {
        return user_id;
    }
    @NotNull
    public String getUsername() {
        return username;
    }
    @NotNull
    public String getSha512password() {
        return sha512password;
    }
    @NotNull
    public String getToken() {
        return token;
    }
    @NotNull
    public Date getTokenExpirationDate() {
        return token_expiration_date;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteUser that)) return false;

        if (!Objects.equals(user_id, that.user_id)) return false;
        if (!getUsername().equals(that.getUsername())) return false;
        if (!getSha512password().equals(that.getSha512password())) return false;
        if (!getToken().equals(that.getToken())) return false;
        return token_expiration_date.equals(that.token_expiration_date);
    }
    @Override
    public int hashCode() {
        int result = user_id != null ? user_id.hashCode() : 0;
        result = 31 * result + getUsername().hashCode();
        result = 31 * result + getSha512password().hashCode();
        result = 31 * result + getToken().hashCode();
        result = 31 * result + token_expiration_date.hashCode();
        return result;
    }
    @Override
    public String toString() {
        return "RemoteUser{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", sha512password='" + sha512password + '\'' +
                ", token='" + token + '\'' +
                ", token_expiration_date=" + token_expiration_date +
                '}';
    }
}

package it.italiandudes.mymcserver.commands.remote;

import it.italiandudes.idl.common.StringHandler;
import it.italiandudes.mymcserver.exceptions.ModuleException;
import it.italiandudes.mymcserver.modules.CommandsModule;
import it.italiandudes.mymcserver.modules.ConnectionModule;
import it.italiandudes.mymcserver.modules.DBConnectionModule;
import it.italiandudes.mymcserver.modules.LocalizationModule;
import it.italiandudes.mymcserver.modules.httphandlers.remote.RemoteUser;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.ServerLogger;
import org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings({"deprecation", "unused"})
public class MMCSRemoteCommand implements CommandExecutor {

    // Command Name
    public static final String COMMAND_NAME = Defs.Commands.MMCS_REMOTE;
    public static final boolean RUN_WITH_MODULE_NOT_LOADED = false;

    // Command Arguments
    public static final class Arguments {
        public static final String ENABLE_REMOTE = "enable";
        public static final String DISABLE_REMOTE = "disable";
        public static final String EDIT_REMOTE = "edit";
    }

    // Command Body
    @SuppressWarnings("DuplicatedCode") @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if (!CommandsModule.isModuleLoaded() && !RUN_WITH_MODULE_NOT_LOADED) {
            try {
                sender.sendMessage(
                    ChatColor.RED +
                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_MODULE_NOT_LOADED)
                );
            } catch (ModuleException ignored) {}
            return true;
        }
        if (args.length < 2) {
            try {
                sender.sendMessage(
                    ChatColor.RED +
                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_INSUFFICIENT_PARAMETERS)
                );
            }catch (ModuleException ignored) {}
            return true;
        }

        try {
            if (sender instanceof Player) {
                switch (args[0]) {
                    case Arguments.ENABLE_REMOTE -> {
                        try {
                            if (args.length == 3) {
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_ENABLE_STARTED)
                                );
                                if (!args[1].equals(args[2])) { // Password mismatch
                                    sender.sendMessage(
                                        ChatColor.RED +
                                        LocalizationModule.translate(Defs.Localization.Keys.COMMAND_PASSWORD_MISMATCH)
                                    );
                                } else {
                                    int result = registerUser(sender.getName(), DigestUtils.sha512Hex(args[1]).toLowerCase());
                                    if (result == 0) {
                                        sender.sendMessage(
                                            ChatColor.AQUA +
                                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_ENABLE_SUCCESS)
                                        );
                                    } else if (result > 0) {
                                        sender.sendMessage(
                                            ChatColor.RED +
                                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_ENABLE_FAIL_ALREADY_ENABLED)
                                        );
                                    }else {
                                        sender.sendMessage(
                                            ChatColor.RED +
                                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_ENABLE_FAIL)
                                        );
                                    }
                                }
                            } else {
                                sender.sendMessage(
                                    ChatColor.RED +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_SYNTAX_ERROR)
                                );
                            }
                        } catch (ModuleException e) {
                            ServerLogger.getLogger().severe("An error has occurred with a module");
                            ServerLogger.getLogger().severe(StringHandler.getStackTrace(e));
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_ENABLE_FAIL)
                            );
                        }
                    }

                    case Arguments.DISABLE_REMOTE -> {
                        try {
                            if (args.length == 2) {
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_DISABLE_STARTED)
                                );
                                int result = unregisterUser(sender.getName(), DigestUtils.sha512Hex(args[1]).toLowerCase());
                                if (result == 0) {
                                    sender.sendMessage(
                                        ChatColor.AQUA +
                                        LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_DISABLE_SUCCESS)
                                    );
                                } else if (result > 0) {
                                    sender.sendMessage(
                                        ChatColor.RED +
                                        LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_DISABLE_FAIL_NOT_ENABLED)
                                    );
                                } else {
                                    sender.sendMessage(
                                        ChatColor.RED +
                                        LocalizationModule.translate(Defs.Localization.Keys.COMMAND_SYNTAX_ERROR)
                                    );
                                }
                            } else {
                                sender.sendMessage(
                                    ChatColor.RED +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_SYNTAX_ERROR)
                                );
                            }
                        } catch (ModuleException e) {
                            ServerLogger.getLogger().severe("An error has occurred with a module");
                            ServerLogger.getLogger().severe(StringHandler.getStackTrace(e));
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_DISABLE_FAIL)
                            );
                        }
                    }

                    case Arguments.EDIT_REMOTE -> {
                        try {
                            if (args.length == 4) {
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_EDIT_STARTED)
                                );
                                if (!args[2].equals(args[3])) {
                                    sender.sendMessage(
                                        ChatColor.RED +
                                        LocalizationModule.translate(Defs.Localization.Keys.COMMAND_PASSWORD_MISMATCH)
                                    );
                                } else {
                                    int result = updateUser(sender.getName(), DigestUtils.sha512Hex(args[1]).toLowerCase(), DigestUtils.sha512Hex(args[2]).toLowerCase());
                                    if (result == 0) {
                                        sender.sendMessage(
                                            ChatColor.AQUA +
                                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_EDIT_SUCCESS)
                                        );
                                    } else if (result > 0) {
                                        sender.sendMessage(
                                            ChatColor.RED +
                                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_EDIT_FAIL_NOT_ENABLED_OR_PASSWORD_WRONG)
                                        );
                                    } else {
                                        sender.sendMessage(
                                            ChatColor.RED +
                                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_EDIT_FAIL)
                                        );
                                    }
                                }
                            } else {
                                sender.sendMessage(
                                    ChatColor.RED +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_SYNTAX_ERROR)
                                );
                            }
                        } catch (ModuleException e) {
                            ServerLogger.getLogger().severe("An error has occurred with a module");
                            ServerLogger.getLogger().severe(StringHandler.getStackTrace(e));
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_EDIT_FAIL)
                            );
                        }
                    }

                    default -> sender.sendMessage(
                            ChatColor.RED +
                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_SYNTAX_ERROR)
                    );
                }
            } else {
                switch (args[0]) {
                    case Arguments.ENABLE_REMOTE -> {
                        try {
                            if (args.length == 4) {
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_ENABLE_STARTED)
                                );
                                if (!args[2].equals(args[3])) { // Password mismatch
                                    sender.sendMessage(
                                        ChatColor.RED +
                                        LocalizationModule.translate(Defs.Localization.Keys.COMMAND_PASSWORD_MISMATCH)
                                    );
                                } else {
                                    int result = registerUser(args[1], args[2].toLowerCase());
                                    if (result == 0) {
                                        sender.sendMessage(
                                            ChatColor.AQUA +
                                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_ENABLE_SUCCESS)
                                        );
                                    } else if (result > 0) {
                                        sender.sendMessage(
                                            ChatColor.RED +
                                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_ENABLE_FAIL_ALREADY_ENABLED)
                                        );
                                    } else {
                                        sender.sendMessage(
                                            ChatColor.RED +
                                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_ENABLE_FAIL)
                                        );
                                    }
                                }
                            } else {
                                sender.sendMessage(
                                    ChatColor.RED +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_SYNTAX_ERROR)
                                );
                            }
                        } catch (ModuleException e) {
                            ServerLogger.getLogger().severe("An error has occurred with a module");
                            ServerLogger.getLogger().severe(StringHandler.getStackTrace(e));
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_ENABLE_FAIL)
                            );
                        }
                    }

                    case Arguments.DISABLE_REMOTE -> {
                        try {
                            if (args.length == 3) {
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_DISABLE_STARTED)
                                );
                                int result = unregisterUser(args[1], args[2].toLowerCase());
                                if (result == 0) {
                                    sender.sendMessage(
                                        ChatColor.AQUA +
                                        LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_DISABLE_SUCCESS)
                                    );
                                } else if (result > 0) {
                                    sender.sendMessage(
                                        ChatColor.RED +
                                        LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_DISABLE_FAIL_NOT_ENABLED)
                                    );
                                } else {
                                    sender.sendMessage(
                                        ChatColor.RED +
                                        LocalizationModule.translate(Defs.Localization.Keys.COMMAND_SYNTAX_ERROR)
                                    );
                                }
                            } else {
                                sender.sendMessage(
                                    ChatColor.RED +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_SYNTAX_ERROR)
                                );
                            }
                        } catch (ModuleException e) {
                            ServerLogger.getLogger().severe("An error has occurred with a module");
                            ServerLogger.getLogger().severe(StringHandler.getStackTrace(e));
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_DISABLE_FAIL)
                            );
                        }
                    }

                    case Arguments.EDIT_REMOTE -> {
                        try {
                            if (args.length == 5) {
                                sender.sendMessage(
                                    ChatColor.AQUA +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_EDIT_STARTED)
                                );
                                if (!args[3].equals(args[4])) {
                                    sender.sendMessage(
                                        ChatColor.RED +
                                        LocalizationModule.translate(Defs.Localization.Keys.COMMAND_PASSWORD_MISMATCH)
                                    );
                                } else {
                                    int result = updateUser(args[1], args[2].toLowerCase(), args[3].toLowerCase());
                                    if (result == 0) {
                                        sender.sendMessage(
                                            ChatColor.AQUA +
                                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_EDIT_SUCCESS)
                                        );
                                    } else if (result > 0) {
                                        sender.sendMessage(
                                            ChatColor.RED +
                                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_EDIT_FAIL_NOT_ENABLED_OR_PASSWORD_WRONG)
                                        );
                                    } else {
                                        sender.sendMessage(
                                            ChatColor.RED +
                                            LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_EDIT_FAIL)
                                        );
                                    }
                                }
                            } else {
                                sender.sendMessage(
                                    ChatColor.RED +
                                    LocalizationModule.translate(Defs.Localization.Keys.COMMAND_SYNTAX_ERROR)
                                );
                            }
                        } catch (ModuleException e) {
                            ServerLogger.getLogger().severe("An error has occurred with a module");
                            ServerLogger.getLogger().severe(StringHandler.getStackTrace(e));
                            sender.sendMessage(
                                ChatColor.RED +
                                LocalizationModule.translate(Defs.Localization.Keys.COMMAND_REMOTE_EDIT_FAIL)
                            );
                        }
                    }

                    default -> sender.sendMessage(
                        ChatColor.RED +
                        LocalizationModule.translate(Defs.Localization.Keys.COMMAND_SYNTAX_ERROR)
                    );
                }
            }
        } catch (ModuleException e) {
            CommandsModule.sendDefaultError(sender, e);
        }
        return true;
    }

    // Methods
    private int registerUser(@NotNull final String username, @NotNull final String sha512password) {
        PreparedStatement ps = null;
        if (isUserRegistered(username)!=null) return 1;
        try {
            RemoteUser remoteUser = ConnectionModule.generateRemoteUser(username, sha512password);
            String query = "INSERT INTO remote_users (username, sha512password, token, token_expiration_date) VALUES (?,?,?,?);";
            ps = DBConnectionModule.getPreparedStatement(query);
            ps.setString(1, remoteUser.getUsername());
            ps.setString(2, remoteUser.getSha512password());
            ps.setString(3, remoteUser.getToken());
            ps.setDate(4, remoteUser.getTokenExpirationDate());
            ps.executeUpdate();
            ps.close();
            return 0;
        } catch (SQLException sqlException) {
            try {
                ps.close();
            }catch (SQLException ignored){}
            return 1;
        }catch (ModuleException e) {
            ServerLogger.getLogger().severe("Module error has occurred");
            ServerLogger.getLogger().severe(StringHandler.getStackTrace(e));
            return -1;
        }
    }
    private int unregisterUser(@NotNull String username, @NotNull final String sha512password) {
        if (!checkPassword(username, sha512password)) return 1;
        PreparedStatement ps = null;
        try {
            String token = ConnectionModule.getUserToken(username, sha512password);
            if (token == null) return 1;
            RemoteUser remoteUser = ConnectionModule.getUserByToken(token);
            if (remoteUser == null || remoteUser.getUserID() == null) return 1;
            String query = "DELETE FROM remote_users WHERE user_id=?;";
            ps = DBConnectionModule.getPreparedStatement(query);
            ps.setInt(1, remoteUser.getUserID());
            ps.executeUpdate();
            ps.close();
            return 0;
        }catch (SQLException sqlException) {
            try {
                ps.close();
            }catch (SQLException ignored){}
            sqlException.printStackTrace();
            return 1;
        }catch (ModuleException e) {
            ServerLogger.getLogger().severe("Module error has occurred");
            ServerLogger.getLogger().severe(StringHandler.getStackTrace(e));
            return -1;
        }
    }
    private int updateUser(@NotNull final String username, @NotNull final String oldSha512password, @NotNull final String newSha512password) {
        if (oldSha512password.equals(newSha512password)) return 0;
        PreparedStatement ps = null;
        if (isUserRegistered(username)==null) return 1;
        if (!checkPassword(username, oldSha512password)) return 1;
        String oldToken = ConnectionModule.getUserToken(username, oldSha512password);
        if (oldToken == null) return 1;
        RemoteUser oldRemoteUser = ConnectionModule.getUserByToken(oldToken);
        if (oldRemoteUser == null || oldRemoteUser.getUserID() == null) return 1;
        RemoteUser newRemoteUser = ConnectionModule.generateRemoteUser(username, newSha512password);
        try {
            String query = "UPDATE remote_users SET sha512password=?, token=?, token_expiration_date=? WHERE user_id=?;";
            ps = DBConnectionModule.getPreparedStatement(query);
            ps.setString(1, newRemoteUser.getSha512password());
            ps.setString(2, newRemoteUser.getToken());
            ps.setDate(3, newRemoteUser.getTokenExpirationDate());
            ps.setInt(4, oldRemoteUser.getUserID());
            ps.executeUpdate();
            ps.close();
            return 0;
        }catch (SQLException sqlException) {
            try {
                ps.close();
            } catch (SQLException ignored){}
            sqlException.printStackTrace();
            return 1;
        }catch (ModuleException e) {
            ServerLogger.getLogger().severe("Error module has occurred");
            ServerLogger.getLogger().severe(StringHandler.getStackTrace(e));
            return -1;
        }
    }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean checkPassword(@NotNull final String username, @NotNull final String sha512password) {
        String query = "SELECT * FROM remote_users WHERE username=? AND sha512password=?;";
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            ps = DBConnectionModule.getPreparedStatement(query);
            ps.setString(1, username);
            ps.setString(2, sha512password);
            result = ps.executeQuery();
            int count = 0;
            while (result.next()) {
                count++;
            }
            result.close();
            ps.close();
            return count == 1;
        } catch (SQLException | ModuleException e) {
            try {
                if (result != null) result.close();
            }catch (SQLException ignored){}
            try {
                if (ps != null) ps.close();
            }catch (SQLException ignored){}
            return false;
        }
    }
    private Integer isUserRegistered(@NotNull final String username) {
        PreparedStatement ps = null;
        ResultSet result = null;
        try {
            String query = "SELECT user_id, token FROM remote_users WHERE username=?;";
            ps = DBConnectionModule.getPreparedStatement(query);
            ps.setString(1, username);
            result = ps.executeQuery();
            Integer userID = null;
            int count = 0;
            while (result.next()) {
                count++;
                userID = result.getInt("user_id");
            }
            result.close();
            ps.close();

            if (count == 1) {
                return userID;
            }
            return null;
        }catch (SQLException | ModuleException e) {
            try {
                if (result != null) result.close();
            }catch (SQLException ignored){}
            try {
                if (ps != null) ps.close();
            }catch (SQLException ignored){}
            return null;
        }
    }
}

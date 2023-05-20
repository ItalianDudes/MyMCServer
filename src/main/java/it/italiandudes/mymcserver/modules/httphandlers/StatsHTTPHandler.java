package it.italiandudes.mymcserver.modules.httphandlers;

import com.sun.management.OperatingSystemMXBean;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import it.italiandudes.mymcserver.modules.ConnectionModule;
import it.italiandudes.mymcserver.modules.httphandlers.remote.RemoteUser;
import it.italiandudes.mymcserver.modules.httphandlers.stats.PlayerDescriptor;
import it.italiandudes.mymcserver.utils.Defs;
import it.italiandudes.mymcserver.utils.Defs.Connection.JSONContent;
import it.italiandudes.mymcserver.utils.Defs.Connection.ReturnCode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
public final class StatsHTTPHandler implements HttpHandler {

    // Context
    public static final String CONTEXT = Defs.Connection.Context.CONTEXT_STATS;

    // Handler
    @Override @SuppressWarnings({"unchecked", "DuplicatedCode"})
    public void handle(HttpExchange exchange) throws IOException {

        // Get Headers
        Headers requestHeaders = exchange.getRequestHeaders();

        // Parsing headers
        List<String> tokenHeader = requestHeaders.get(Defs.Connection.Header.HEADER_TOKEN);

        if (tokenHeader == null || tokenHeader.size() != 1) { // The request is invalid
            ConnectionModule.CommonResponse.sendBadRequest(exchange);
            exchange.close();
            return;
        }

        // Getting required data
        String token = tokenHeader.get(0);

        // Getting the username from the token, to validate if the user exist
        RemoteUser remoteUser = ConnectionModule.getUserByToken(token);
        if (remoteUser == null) {
            ConnectionModule.CommonResponse.sendInternalServerError(exchange);
            exchange.close();
            return;
        }

        // Prepare JSON result
        JSONObject response = new JSONObject();
        response.put(JSONContent.RETURN_CODE, ReturnCode.OK);

        // Get system stats
        OperatingSystemMXBean operatingSystem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double systemCPULoad = operatingSystem.getCpuLoad();
        double processCPULoad = operatingSystem.getProcessCpuLoad();
        double totalMemory = operatingSystem.getTotalMemorySize();
        double freeMemory = operatingSystem.getFreeMemorySize();
        double committedVirtualMemory = operatingSystem.getCommittedVirtualMemorySize();

        // Bundling system stats into JSON
        response.put(JSONContent.SYSTEM_CPU_LOAD, systemCPULoad);
        response.put(JSONContent.PROCESS_CPU_LOAD, processCPULoad);
        response.put(JSONContent.TOTAL_MEMORY, totalMemory);
        response.put(JSONContent.FREE_MEMORY, freeMemory);
        response.put(JSONContent.COMMITTED_VIRTUAL_MEMORY, committedVirtualMemory);

        // Get players stats
        List<Player> onlinePlayerList = (List<Player>) Bukkit.getServer().getOnlinePlayers();
        List<OfflinePlayer> offlinePlayerList = Arrays.stream(Bukkit.getServer().getOfflinePlayers()).toList();
        HashSet<PlayerDescriptor> playerSet = new HashSet<>();
        for (Player player : onlinePlayerList) {
            playerSet.add(new PlayerDescriptor(player));
        }
        for (OfflinePlayer offlinePlayer : offlinePlayerList) {
            playerSet.add(new PlayerDescriptor(offlinePlayer));
        }
        JSONArray players = new JSONArray();
        for (PlayerDescriptor playerDescriptor : playerSet) {
            JSONObject content = playerDescriptor.getJSON();
            if (content != null) players.add(content);
        }

        // Bundling players stats into JSON
        response.put(JSONContent.PLAYER_LIST, players);

        // Get plugins stats
        Plugin[] installedPlugins = Bukkit.getPluginManager().getPlugins();
        JSONArray pluginsArray = new JSONArray();
        for (Plugin plugin: installedPlugins) {
            JSONObject singlePlugin = new JSONObject();
            singlePlugin.put(JSONContent.AddonsList.NAME, plugin.getName());
            singlePlugin.put(JSONContent.AddonsList.ENABLED, plugin.isEnabled());
            pluginsArray.add(singlePlugin);
        }

        // Bundling plugins stats into JSON
        response.put(JSONContent.ADDONS_LIST, pluginsArray);

        // Send the response
        ConnectionModule.sendHTTPResponse(exchange, response);

        // Finishing the transaction
        exchange.close();
    }
}

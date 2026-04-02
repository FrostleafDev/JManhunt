package de.jozelot.jmanhunt.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.jozelot.jmanhunt.JManhunt;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateManager {

    private final JManhunt plugin;
    private final String currentVersion;
    private String latestVersion;
    private boolean updateAvailable = false;

    public UpdateManager(@NotNull JManhunt plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.github.com/repos/FrostleafDev/JManhunt/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "JManhunt-Update-Checker");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                        if (json.has("tag_name")) {
                            this.latestVersion = json.get("tag_name").getAsString().replace("v", "");
                            this.updateAvailable = isNewer(currentVersion, latestVersion);

                            if (updateAvailable) {
                                plugin.getLogger().warning("A new version of JManhunt is available: " + latestVersion);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Update couldn't be checked: " + e.getMessage());
            }
        });
    }

    private boolean isNewer(String local, String remote) {
        String[] localParts = local.split("\\.");
        String[] remoteParts = remote.split("\\.");
        int length = Math.max(localParts.length, remoteParts.length);

        for (int i = 0; i < length; i++) {
            int l = i < localParts.length ? Integer.parseInt(localParts[i]) : 0;
            int r = i < remoteParts.length ? Integer.parseInt(remoteParts[i]) : 0;
            if (r > l) return true;
            if (l > r) return false;
        }
        return false;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }
}
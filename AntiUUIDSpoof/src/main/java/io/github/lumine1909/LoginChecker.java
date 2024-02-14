package io.github.lumine1909;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static io.github.lumine1909.AntiUUIDSpoof.*;

public class LoginChecker implements Listener {
    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        if (!checkUUID(e.getName(), e.getUniqueId())) {
            e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, KICK_MESSAGE);
        }
    }
    public boolean checkUUID(String name, UUID uuid) {
        boolean validOff = false, validOn = false;
        if ((CHECKER_MODE & 1) == 1) {
            UUID offlineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
            if (uuid.equals(offlineUUID)) {
                validOff = true;
            }
            if (DEBUG) {
                plugin.getLogger().info("The offline UUID of player " + name + " is " + offlineUUID);
            }
        }
        if ((CHECKER_MODE & 2) == 2) {
            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                JsonObject obj = JsonParser.parseReader(new InputStreamReader(url.openStream())).getAsJsonObject();
                String pname = obj.get("name").getAsString();
                String rawUUID = obj.get("id").getAsString();
                UUID onlineUUID = UUID.fromString(rawUUID.substring(0, 8) + "-" +
                        rawUUID.substring(8, 12) + "-" + rawUUID.substring(12, 16) + "-" +
                        rawUUID.substring(16, 20) + "-" + rawUUID.substring(20, 32));
                if (DEBUG) {
                    plugin.getLogger().info("The online UUID of player " + pname + " is " + onlineUUID);
                }
                if (uuid.equals(onlineUUID)) {
                    validOn = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (validOn || validOff);
    }
}

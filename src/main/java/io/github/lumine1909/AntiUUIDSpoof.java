package io.github.lumine1909;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

public class AntiUUIDSpoof extends JavaPlugin {
    public static boolean isBungee;
    public static int CHECKER_MODE;
    public static String KICK_MESSAGE;
    public static boolean DEBUG;
    public static JavaPlugin plugin;
    @Override
    public void onLoad() {
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        plugin = this;
        reload(null);
        try {
            Class<?> spigotConfigC = Class.forName("org.spigotmc.SpigotConfig");
            Field isBungeeF = spigotConfigC.getDeclaredField("bungee");
            isBungeeF.setAccessible(true);
            isBungee = (boolean) isBungeeF.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().info("Failed to check bungeecord info!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (!isBungee) {
            getLogger().info("You are not using bungeecord forwarding, plugin disabled!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(new LoginChecker(), this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length >= 1 && args[0].equals("reload") && sender.hasPermission("handshakechecker.reload")) {
            reload(sender);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length < 1) {
            return Collections.singletonList("reload");
        }
        return Collections.emptyList();
    }

    public void reload(CommandSender sender) {
        reloadConfig();
        CHECKER_MODE = getConfig().getInt("checker-mode", 1);
        KICK_MESSAGE = getConfig().getString("kick-message", "Illegal UUID or Username");
        DEBUG = getConfig().getBoolean("debug", false);
        if (sender != null) {
            sender.sendMessage(ChatColor.AQUA + "config reloaded");
        }
    }
}

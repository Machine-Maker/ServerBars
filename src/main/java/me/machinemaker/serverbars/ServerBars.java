package me.machinemaker.serverbars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ServerBars extends JavaPlugin implements Listener, CommandExecutor {

//    private AnimatedBar defaultBar = null;
    private Map<World, AnimatedBar> worldBars;

    private List<String> blacklistedWorlds;

    @Override
    public void onEnable() {
        worldBars = new HashMap<>();
        blacklistedWorlds = new ArrayList<>();

        File config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            if (getDataFolder().mkdirs())
                getLogger().info("Created the plugin folder");
            saveResource("config.yml", false);
        }

        getServer().getPluginManager().registerEvents(this, this);
        reloadBars();
    }

    @Override
    public void onDisable() {
        worldBars.values().forEach(bar -> bar.getBossBar().removeAll());
    }

    private void reloadBars() {
        worldBars.clear();
        for (World world : Bukkit.getWorlds()) {
            if (blacklistedWorlds.contains(world.getName())) continue;
            String path = "default-bar";
            if (getConfig().isSet("world-bars." + world.getName())) {
                path = "world-bars." + world.getName();
            }
            List<Bar> bars = new ArrayList<>();
            List<Map<?, ?>> barConfig = getConfig().getMapList(path);
            for (Map<?, ?> map : barConfig) {
                String title = null;
                if (map.get("title") != null) title = ChatColor.translateAlternateColorCodes('&', (String) map.get("title"));
                BarColor color = null;
                if (map.get("color") != null) color = BarColor.valueOf(((String) map.get("color")).toUpperCase());
                BarStyle style = null;
                if (map.get("style") != null) style = BarStyle.valueOf(((String) map.get("style")).toUpperCase());
                long ticks = Long.parseLong(String.valueOf(map.get("time")));
                bars.add(new Bar(title, color, style, ticks));
            }
            worldBars.put(world, new AnimatedBar(bars, this));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("serverbars.reload")) {
            worldBars.values().forEach(bar -> bar.getBossBar().removeAll());
            reloadConfig();
            reloadBars();
            blacklistedWorlds = getConfig().getStringList("blacklist");
            sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.RED + ChatColor.BOLD + "S" + ChatColor.BLUE + ChatColor.BOLD + "B" + ChatColor.GRAY + "] " + ChatColor.GREEN + "Configuration reloaded!");
            for (Player player : Bukkit.getOnlinePlayers())
                if (worldBars.containsKey(player.getWorld()))
                    worldBars.get(player.getWorld()).getBossBar().addPlayer(player);
        }
        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (worldBars.containsKey(event.getPlayer().getWorld()))
            worldBars.get(event.getPlayer().getWorld()).getBossBar().addPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (worldBars.containsKey(event.getFrom()))
            worldBars.get(event.getFrom()).getBossBar().removePlayer(event.getPlayer());
        if (worldBars.containsKey(event.getPlayer().getWorld()))
            worldBars.get(event.getPlayer().getWorld()).getBossBar().addPlayer(event.getPlayer());
    }
}

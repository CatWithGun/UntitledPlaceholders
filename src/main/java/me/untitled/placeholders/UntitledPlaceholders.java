package me.untitled.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public final class UntitledPlaceholders extends JavaPlugin implements Listener {

    private int maxOnline;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        maxOnline = getConfig().getInt("max-online", Bukkit.getOnlinePlayers().size());
        Bukkit.getPluginManager().registerEvents(this, this);
        new UntitledExpansion(this).register();
        updateMaxOnline();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTask(this, this::updateMaxOnline);
    }

    private void updateMaxOnline() {
        int currentOnline = Bukkit.getOnlinePlayers().size();
        if (currentOnline > maxOnline) {
            maxOnline = currentOnline;
            getConfig().set("max-online", maxOnline);
            saveConfig();
        }
    }

    private static class UntitledExpansion extends PlaceholderExpansion {
        private final UntitledPlaceholders plugin;

        private UntitledExpansion(UntitledPlaceholders plugin) {
            this.plugin = plugin;
        }

        @Override
        public String getIdentifier() {
            return "untitled";
        }

        @Override
        public String getAuthor() {
            return "Untitled";
        }

        @Override
        public String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(Player player, String params) {
            if (params.equalsIgnoreCase("max_online")) {
                return String.valueOf(plugin.maxOnline);
            }

            if (params.equalsIgnoreCase("kd")) {
                if (player == null) {
                    return "0.00";
                }
                int kills = player.getStatistic(Statistic.PLAYER_KILLS);
                int deaths = player.getStatistic(Statistic.DEATHS);
                double kd = deaths == 0 ? kills : (double) kills / deaths;
                return String.format(Locale.US, "%.2f", kd);
            }

            return null;
        }
    }
}

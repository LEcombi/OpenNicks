package de.prime4g.openNicks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class OpenNicksExpansion extends PlaceholderExpansion {

    private final OpenNicks plugin;

    public OpenNicksExpansion(OpenNicks plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "opennicks";
    }

    @Override
    public @NotNull String getAuthor() {
        return "LEcode";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true; // bleibt nach /papi reload registriert
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "";

        switch (identifier.toLowerCase()) {
            case "nick":
                String nick = null;
                try {
                    nick = plugin.dbManager.getNick(player.getUniqueId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return nick != null ? nick : player.getName();

            case "real_name":
                return player.getName();

            default:
                return null;
        }
    }
}

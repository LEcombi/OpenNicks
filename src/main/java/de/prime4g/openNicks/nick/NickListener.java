package de.prime4g.openNicks.nick;

import de.prime4g.openNicks.OpenNicks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class NickListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        setNickIfExists(player);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        setNickIfExists(player);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        setNickIfExists(player);
    }

    public static String setNickIfExists(Player player) {
        String nick = NickCommand.getNick(player.getUniqueId());
        if (nick == null) {
            try {
                nick = OpenNicks.getInstance().dbManager.getNick(player.getUniqueId());
                if (nick != null) {
                    NickCommand.setNick(player.getUniqueId(), nick);
                }
            } catch (Exception ignored) {}
        }
        if (nick != null) {
            player.setDisplayName(nick);
            player.setPlayerListName(nick);
        }
        return nick;
    }
}


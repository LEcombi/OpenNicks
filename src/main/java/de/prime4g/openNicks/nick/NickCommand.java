package de.prime4g.openNicks.nick;

import de.prime4g.openNicks.OpenNicks;
import de.prime4g.openNicks.Database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NickCommand implements CommandExecutor {
    // Nick cache
    private static final Map<UUID, String> nicks = new HashMap<>();
    private static DatabaseManager db() { return OpenNicks.getInstance().dbManager; }

    // Initialize cache (e.g. on server start)
    public static void loadAllNicks() {
        try {
            db().createNickTableIfNotExists();
            nicks.clear();
            nicks.putAll(db().getAllNicks());
        } catch (Exception e) {
            Bukkit.getLogger().warning("OpenNicks: Error loading nicks: " + e.getMessage());
        }
    }

    public static String getNick(UUID uuid) {
        return nicks.get(uuid);
    }

    public static void removeNick(UUID uuid) {
        nicks.remove(uuid);
        try { db().removeNick(uuid); } catch (Exception ignored) {}
    }

    public static boolean isNickTaken(String nick) {
        try { return db().isNickTaken(nick); } catch (Exception e) { return false; }
    }

    public static void setNick(UUID uuid, String nick) throws SQLException {
        nicks.put(uuid, nick);
        db().setNick(uuid, nick);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.YELLOW + "OpenNicks » Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;

        // Admin command: /nick set <playername> <nick>
        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            if (!player.hasPermission("OpenNicks.admin")) {
                player.sendMessage(ChatColor.RED + "OpenNicks » You do not have permission to use this command.");
                return true;
            }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(ChatColor.YELLOW + "OpenNicks » Player not found.");
                return true;
            }
            String nick = ChatColor.translateAlternateColorCodes('&', args[2]);
            if (isNickTaken(nick)) {
                player.sendMessage(ChatColor.YELLOW + "OpenNicks » This nickname is already taken!");
                return true;
            }
            try {
                setNick(target.getUniqueId(), nick);
                target.setDisplayName(nick);
                target.setPlayerListName(nick);
                player.sendMessage(ChatColor.YELLOW + "OpenNicks » Set nickname for " + target.getName() + " to: " + nick);
                target.sendMessage(ChatColor.YELLOW + "OpenNicks » Your nickname is now: " + nick);
            } catch (Exception e) {
                player.sendMessage(ChatColor.YELLOW + "OpenNicks » Error while setting the nickname.");
            }
            return true;
        }

        // Normal user command
        if (args.length == 0) {
            player.sendMessage(ChatColor.YELLOW + "OpenNicks » Usage: /nick <name|off>");
            return true;
        }
        if (args[0].equalsIgnoreCase("off")) {
            removeNick(player.getUniqueId());
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            player.sendMessage(ChatColor.YELLOW + "OpenNicks » Your nickname has been removed.");
            return true;
        }
        String nick = ChatColor.translateAlternateColorCodes('&', args[0]);
        if (isNickTaken(nick)) {
            player.sendMessage(ChatColor.YELLOW + "OpenNicks » This nickname is already taken!");
            return true;
        }
        try {
            setNick(player.getUniqueId(), nick);
            player.setDisplayName(nick);
            player.setPlayerListName(nick);
            player.sendMessage(ChatColor.YELLOW + "OpenNicks » Your nickname is now: " + nick);
        } catch (Exception e) {
            player.sendMessage(ChatColor.YELLOW + "OpenNicks » Error while setting the nickname.");
        }
        return true;
    }
}
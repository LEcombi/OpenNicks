package de.prime4g.openNicks.Database;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseManager {
    private final Connection connection;

    public DatabaseManager(JavaPlugin plugin) throws SQLException {
        FileConfiguration config = plugin.getConfig();
        String url = "jdbc:mysql://" + config.getString("db.host") + ":" + config.getInt("db.port") + "/" + config.getString("db.name");
        String user = config.getString("db.user");
        String pass = config.getString("db.password");
        connection = DriverManager.getConnection(url, user, pass);
        createNickTableIfNotExists();
    }

    public void createNickTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS nick (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "nickname VARCHAR(32) UNIQUE NOT NULL)";
        connection.createStatement().execute(sql);
    }

    public void setNick(UUID uuid, String nickname) throws SQLException {
        String sql = "REPLACE INTO nick (uuid, nickname) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.setString(2, nickname);
            ps.executeUpdate();
        }
    }

    public void removeNick(UUID uuid) throws SQLException {
        String sql = "DELETE FROM nick WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        }
    }

    public String getNick(UUID uuid) throws SQLException {
        String sql = "SELECT nickname FROM nick WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("nickname");
            }
        }
        return null;
    }

    public boolean isNickTaken(String nickname) throws SQLException {
        String sql = "SELECT uuid FROM nick WHERE nickname = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Map<UUID, String> getAllNicks() throws SQLException {
        Map<UUID, String> map = new HashMap<>();
        String sql = "SELECT uuid, nickname FROM nick";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(UUID.fromString(rs.getString("uuid")), rs.getString("nickname"));
            }
        }
        return map;
    }
}


package dev.al3mid3x.punishments.database.provider;

import dev.al3mid3x.punishments.Punishments;
import dev.al3mid3x.punishments.database.Database;
import dev.al3mid3x.punishments.managers.ConfigManager;
import dev.al3mid3x.punishments.models.Punishment;
import dev.al3mid3x.punishments.utils.PunishmentType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQLManager implements Database {

    private Connection connection;
    private final Punishments plugin;
    private final ConfigManager configManager;

    public MySQLManager(Punishments plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @Override
    public void connect() {
        String host = configManager.getString("database.host", "localhost");
        int port = configManager.getConfig().getInt("database.port", 3306);
        String databaseName = configManager.getString("database.database", "punishments");
        String username = configManager.getString("database.username", "root");
        String password = configManager.getString("database.password", "");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName + "?useSSL=false", username, password);
            createTables();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS punishments (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "uuid VARCHAR(36) NOT NULL," +
                    "type VARCHAR(16) NOT NULL," +
                    "reason TEXT NOT NULL," +
                    "duration BIGINT NOT NULL," +
                    "staff VARCHAR(36) NOT NULL," +
                    "timestamp BIGINT NOT NULL)");

            statement.execute("CREATE TABLE IF NOT EXISTS ip_bans (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "ip VARCHAR(45) NOT NULL," +
                    "reason TEXT NOT NULL," +
                    "staff VARCHAR(36) NOT NULL," +
                    "timestamp BIGINT NOT NULL)");

            statement.execute("CREATE TABLE IF NOT EXISTS ip_history (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "uuid VARCHAR(36) NOT NULL," +
                    "ip VARCHAR(45) NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePunishment(Punishment punishment) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO punishments (uuid, type, reason, duration, staff, timestamp) VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setString(1, punishment.getUuid().toString());
            statement.setString(2, punishment.getType().toString());
            statement.setString(3, punishment.getReason());
            statement.setLong(4, punishment.getDuration());
            statement.setString(5, punishment.getStaff().toString());
            statement.setLong(6, punishment.getTimestamp());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Punishment getPunishment(UUID uuid, PunishmentType type) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM punishments WHERE uuid = ? AND type = ? AND (duration = -1 OR duration > ?)")) {
            statement.setString(1, uuid.toString());
            statement.setString(2, type.toString());
            statement.setLong(3, System.currentTimeMillis());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Punishment(
                        UUID.fromString(resultSet.getString("uuid")),
                        PunishmentType.valueOf(resultSet.getString("type")),
                        resultSet.getString("reason"),
                        resultSet.getLong("duration"),
                        UUID.fromString(resultSet.getString("staff")),
                        resultSet.getLong("timestamp")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Punishment> getPunishments(UUID uuid) {
        List<Punishment> punishments = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM punishments WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                punishments.add(new Punishment(
                        UUID.fromString(resultSet.getString("uuid")),
                        PunishmentType.valueOf(resultSet.getString("type")),
                        resultSet.getString("reason"),
                        resultSet.getLong("duration"),
                        UUID.fromString(resultSet.getString("staff")),
                        resultSet.getLong("timestamp")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishments;
    }

    @Override
    public void removePunishment(Punishment punishment) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM punishments WHERE uuid = ? AND type = ?")) {
            statement.setString(1, punishment.getUuid().toString());
            statement.setString(2, punishment.getType().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePlayerIP(UUID uuid, String ip) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO ip_history (uuid, ip) VALUES (?, ?)")) {
            statement.setString(1, uuid.toString());
            statement.setString(2, ip);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPlayerIP(UUID uuid) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT ip FROM ip_history WHERE uuid = ? ORDER BY id DESC LIMIT 1")) {
            statement.setString(1, uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("ip");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

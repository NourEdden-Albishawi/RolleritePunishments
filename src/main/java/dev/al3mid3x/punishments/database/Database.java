package dev.al3mid3x.punishments.database;

import dev.al3mid3x.punishments.models.Punishment;
import dev.al3mid3x.punishments.utils.PunishmentType;

import java.util.List;
import java.util.UUID;

public interface Database {

    void connect();

    void disconnect();

    void savePunishment(Punishment punishment);

    Punishment getPunishment(UUID uuid, PunishmentType type);

    List<Punishment> getPunishments(UUID uuid);

    void removePunishment(Punishment punishment);

    void savePlayerIP(UUID uuid, String ip);

    String getPlayerIP(UUID uuid);
}

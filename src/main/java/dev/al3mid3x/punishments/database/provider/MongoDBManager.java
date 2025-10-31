package dev.al3mid3x.punishments.database.provider;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.al3mid3x.punishments.Punishments;
import dev.al3mid3x.punishments.database.Database;
import dev.al3mid3x.punishments.managers.ConfigManager;
import dev.al3mid3x.punishments.models.Punishment;
import dev.al3mid3x.punishments.utils.PunishmentType;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoDBManager implements Database {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> punishmentsCollection;
    private MongoCollection<Document> ipBansCollection;
    private MongoCollection<Document> ipHistoryCollection;

    private final Punishments plugin;
    private final ConfigManager configManager;

    public MongoDBManager(Punishments plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;
    }

    @Override
    public void connect() {
        String uri = configManager.getString("database.uri", "mongodb://localhost:27017");
        String databaseName = configManager.getString("database.database", "punishments");

        try {
            mongoClient = MongoClients.create(uri);
            database = mongoClient.getDatabase(databaseName);
            punishmentsCollection = database.getCollection("punishments");
            ipBansCollection = database.getCollection("ip_bans");
            ipHistoryCollection = database.getCollection("ip_history");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Override
    public void savePunishment(Punishment punishment) {
        Document document = new Document()
                .append("uuid", punishment.getUuid().toString())
                .append("type", punishment.getType().toString())
                .append("reason", punishment.getReason())
                .append("duration", punishment.getDuration())
                .append("staff", punishment.getStaff().toString())
                .append("timestamp", punishment.getTimestamp());

        punishmentsCollection.replaceOne(
                Filters.and(
                        Filters.eq("uuid", punishment.getUuid().toString()),
                        Filters.eq("type", punishment.getType().toString())
                ),
                document,
                new ReplaceOptions().upsert(true)
        );
    }

    @Override
    public Punishment getPunishment(UUID uuid, PunishmentType type) {
        Document document = punishmentsCollection.find(
                Filters.and(
                        Filters.eq("uuid", uuid.toString()),
                        Filters.eq("type", type.toString()),
                        Filters.or(
                                Filters.eq("duration", -1L),
                                Filters.gt("duration", System.currentTimeMillis())
                        )
                )
        ).first();

        if (document != null) {
            return new Punishment(
                    UUID.fromString(document.getString("uuid")),
                    PunishmentType.valueOf(document.getString("type")),
                    document.getString("reason"),
                    document.getLong("duration"),
                    UUID.fromString(document.getString("staff")),
                    document.getLong("timestamp")
            );
        }
        return null;
    }

    @Override
    public List<Punishment> getPunishments(UUID uuid) {
        List<Punishment> punishments = new ArrayList<>();
        for (Document document : punishmentsCollection.find(Filters.eq("uuid", uuid.toString()))) {
            punishments.add(new Punishment(
                    UUID.fromString(document.getString("uuid")),
                    PunishmentType.valueOf(document.getString("type")),
                    document.getString("reason"),
                    document.getLong("duration"),
                    UUID.fromString(document.getString("staff")),
                    document.getLong("timestamp")
            ));
        }
        return punishments;
    }

    @Override
    public void removePunishment(Punishment punishment) {
        punishmentsCollection.deleteOne(
                Filters.and(
                        Filters.eq("uuid", punishment.getUuid().toString()),
                        Filters.eq("type", punishment.getType().toString())
                )
        );
    }

    @Override
    public void savePlayerIP(UUID uuid, String ip) {
        Document document = new Document()
                .append("uuid", uuid.toString())
                .append("ip", ip);

        ipHistoryCollection.replaceOne(
                Filters.eq("uuid", uuid.toString()),
                document,
                new ReplaceOptions().upsert(true)
        );
    }

    @Override
    public String getPlayerIP(UUID uuid) {
        Document document = ipHistoryCollection.find(Filters.eq("uuid", uuid.toString())).first();
        if (document != null) {
            return document.getString("ip");
        }
        return null;
    }
}

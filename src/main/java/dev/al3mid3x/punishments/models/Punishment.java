package dev.al3mid3x.punishments.models;

import dev.al3mid3x.punishments.utils.PunishmentType;

import java.util.UUID;

public class Punishment {

    private final UUID uuid;
    private final PunishmentType type;
    private final String reason;
    private final long duration;
    private final UUID staff;
    private final long timestamp;

    public Punishment(UUID uuid, PunishmentType type, String reason, long duration, UUID staff, long timestamp) {
        this.uuid = uuid;
        this.type = type;
        this.reason = reason;
        this.duration = duration;
        this.staff = staff;
        this.timestamp = timestamp;
    }

    public UUID getUuid() {
        return uuid;
    }

    public PunishmentType getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public long getDuration() {
        return duration;
    }

    public UUID getStaff() {
        return staff;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

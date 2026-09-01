package com.vessel.smp.data;

import java.util.UUID;

public class PlayerData {

    public static final int FLOOR = -2;
    public static final int BASELINE = -1;
    public static final int MAX_ESSENCE = 5;

    private final UUID uuid;
    private String username;
    private SpiritType spirit;
    private int essenceLevel;

    public PlayerData(UUID uuid, String username, SpiritType spirit, int essenceLevel) {
        this.uuid = uuid;
        this.username = username;
        this.spirit = spirit;
        this.essenceLevel = essenceLevel;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public SpiritType getSpirit() {
        return spirit;
    }

    public void setSpirit(SpiritType spirit) {
        this.spirit = spirit;
    }

    public int getEssenceLevel() {
        return essenceLevel;
    }

    public void setEssenceLevel(int essenceLevel) {
        this.essenceLevel = essenceLevel;
    }

    public boolean isFloored() {
        return essenceLevel <= FLOOR;
    }

    public boolean absorb() {
        if (essenceLevel >= MAX_ESSENCE) {
            return false;
        }
        essenceLevel++;
        return true;
    }

    public void withdraw() {
        essenceLevel = Math.max(essenceLevel - 1, BASELINE);
    }

    public boolean applyDeathPenalty() {
        if (essenceLevel <= FLOOR) {
            return false;
        }
        essenceLevel = Math.max(essenceLevel - 1, FLOOR);
        return true;
    }

    public void completeRitual() {
        this.essenceLevel = BASELINE;
    }
}

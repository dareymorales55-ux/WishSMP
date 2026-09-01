
package com.vessel.smp.data;

public enum SpiritType {

    WARDEN("Warden"),
    DOLPHIN("Dolphin"),
    VILLAGER("Villager"),
    TURTLE("Turtle");

    private final String displayName;

    SpiritType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SpiritType random() {
        SpiritType[] values = values();
        return values[(int) (Math.random() * values.length)];
    }

    public static SpiritType randomExcluding(SpiritType exclude) {
        SpiritType result;
        do {
            result = random();
        } while (result == exclude);
        return result;
    }
}

package ru.ittim.openhab.ledbinding;

/**
 * Created by Timofey on 23.06.2016.
 */
public enum PowerState {
    UNKNOWN(0x00),
    ON(0x23),
    OFF(0x24),;

    private final int id;

    PowerState(int id) {
        this.id = id;
    }

    public static PowerState get(int id) {
        for (PowerState state : values()) {
            if (state.id == id) {
                return state;
            }
        }
        return UNKNOWN;
    }
}

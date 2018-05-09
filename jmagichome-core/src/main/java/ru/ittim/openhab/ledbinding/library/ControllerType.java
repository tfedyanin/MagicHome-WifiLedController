package ru.ittim.openhab.ledbinding.library;

/**
 * Controller mode
 * RGB - it is possible to set rgb color, warn and cold white separately
 * DIM - another mod (not supported yet)
 * Created by Timofey on 23.06.2016.
 */
public enum ControllerType {
    UNKNOWN(0xffff),
    RGB((byte)0xf0),
    DIM((byte)0x0f);

    private final int id;

    ControllerType(int id) {
        this.id = id;
    }

    public static ControllerType get(int id) {
        for (ControllerType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return UNKNOWN;
    }


}

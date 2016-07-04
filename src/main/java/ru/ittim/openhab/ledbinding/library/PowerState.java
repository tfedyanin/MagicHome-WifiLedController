package ru.ittim.openhab.ledbinding.library;

/**
 * Power state of controller
 * Created by Timofey on 23.06.2016.
 */
public enum PowerState {
    UNKNOWN(0xffff),
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

    public byte[] getCommand() {
        if (this.equals(UNKNOWN)) {
            throw new  UnsupportedOperationException("Can't set UNKNOWN state. Use ON or OFF states.");
        }
        byte[] bytes = new byte[3];
        bytes[0] =(byte)0x71;
        bytes[1] =(byte)id;
        bytes[2] =(byte)0x0f;

        return Utils.withCheckSum(bytes);
    }
}

package ru.ittim.openhab.ledbinding.library;

/**
 * Functional modes for RGB, RGBW, RGBWW
 * Created by Timofey on 23.06.2016.
 */
public enum FunctionalModeRgb {
    // TODO: 23.06.2016 Music, Microphone, Photo
    // Todo Preset mode
    UNKNOWN(0xffff),
    NONE(0x61),
    SEVEN_COLOR(0x25),
    RED_GRADUAL_CHAGE(0x26),
    GREEN_GRADUAL_CHAGE(0x27),
    BLUE_GRADUAL_CHAGE(0x28),
    YELLOW_GRADUAL_CHAGE(0x29),
    CYAN_GRADUAL_CHAGE(0x2a),
    PURPLE_GRADUAL_CHAGE(0x2b),
    WHITE_GRADUAL_CHAGE(0x2c),
    RED_GREEN_CROSS_FADE(0x2d),
    RED_BLUE_CROSS_FADE(0x2e),
    GREEN_BLUE_CROSS_FADE(0x2f),
    SEVEN_COLOR_STROBE_FADE(0x30),
    RED_STROBE_FADE(0x31),
    GREEN_STROBE_FADE(0x32),
    BLUE_STROBE_FADE(0x33),
    YELLOW_STROBE_FADE(0x34),
    CYAN_STROBE_FADE(0x35),
    PURPLE_STROBE_FADE(0x36),
    WHITE_STROBE_FADE(0x37),
    SEVEN_COLOR_CROSS_JUMP(0x38);

    private final int id;
    private int speed = 0x1f;

    FunctionalModeRgb(int id) {
        this.id = id;
    }

    public static FunctionalModeRgb get(int id) {
        for (FunctionalModeRgb mode : values()) {
            if (id == mode.id) {
                return mode;
            }
        }
        return UNKNOWN;
    }

    public int getSpeed() {
        return speed;
    }

    public int getPercentSpeed() {
        return (-speed + 31) * 10 / 3;
    }

    public FunctionalModeRgb setSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    public FunctionalModeRgb setPercentSpeed(int speed) {
        if ((speed < 0) || (speed > 100)) {
            throw new IllegalArgumentException("Скорость должяна быть в интервале [0, 100]");
        }
        this.speed = -(speed * 3 / 10 - 31);
        return this;
    }

    public byte[] getCommand() {
        if ((this.id >= 0x25) && (this.id <= 0x38)) {
            byte[] command = new byte[4];
            command[0] = 0x61;
            command[1] = (byte) this.id;
            command[2] = (byte) this.speed;
            command[3] = 0x0f;
            return Utils.withCheckSum(command);
        }
        throw new UnsupportedOperationException("Для данного мода не сущетвует команды");
    }
}

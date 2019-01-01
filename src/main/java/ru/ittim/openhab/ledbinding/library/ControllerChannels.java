package ru.ittim.openhab.ledbinding.library;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Immutable channels state (r, g, b, cw, ww).
 * Created by Timofey on 24.06.2016.
 */
public class ControllerChannels {
    /**
     * red value from 0 to 255.
     */
    private final byte r;

    /**
     * green value from 0 to 255.
     */
    private final byte g;

    /**
     * blue value from 0 to 255.
     */
    private final byte b;

    /**
     * warn white value from 0 to 255.
     */
    private final byte ww;
    /**
     * cold value value from 0 to 255.
     */
    private final byte cw;

    /**
     * Costructor for create full state.
     * @param r value of red channel power.
     * @param g value of green channel power.
     * @param b value of blue channel power.
     * @param ww value of warn white channel power.
     * @param cw value of cold white channel power.
     */
    ControllerChannels(byte r, byte g, byte b, byte ww, byte cw) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.ww = ww;
        this.cw = cw;
    }

    /**
     * Factory method for build channels from percents values.
     * @param r red channel power in percents.
     * @param g green channel power in percents.
     * @param b blue channel power in percents.
     * @param ww warn white channel power in percents.
     * @param cw cold white channel power in percents.
     * @return channels.
     */
    public static ControllerChannels fromPercents(int r, int g, int b, int ww, int cw) {
        return new ControllerChannels(
                fromPercent(r),
                fromPercent(g),
                fromPercent(b),
                fromPercent(ww),
                fromPercent(cw)
        );
    }

    /**
     * Convert percent value to byte value.
     * @param p percent value.
     * @return byte value: 0x00 from 0x00, 0xFF from 100%.
     */
    private static byte fromPercent(int p) {
        if (p < 0){
            throw new RuntimeException("Percent can't be lower than 0");
        }
        if (p > 100) {
            throw new RuntimeException("Percent can't be greater than 100");
        }
        return (byte) (p / 100.0 * 255);
    }

    /**
     * Convert byte value [0, 255] into percents.
     * @param v byte values
     * @return percent: 0% from 0x00, 100% from 0xFF.
     */
    private int toPercent(byte v){
        return (int) ((v & 0XFF) / 255.0 * 100);
    }

    /**
     * @return red power in percents.
     */
    public int getR() {
        return toPercent(r);
    }

    /**
     * @return green power in percents.
     */
    public int getG() {
        return toPercent(g);
    }

    /**
     * @return blue power in percents.
     */
    public int getB() {
        return toPercent(b);
    }

    /**
     * @return warn white power in percents.
     */
    public int getWw() {
        return toPercent(ww);
    }

    /**
     * @return cold white power in percents.
     */
    public int getCw() {
        return toPercent(cw);
    }

    @Override
    public String toString() {
        return "ControllerChannels{" +
                "r=" + String.format("%02X ", r) +
                ", g=" + String.format("%02X ", g) +
                ", b=" + String.format("%02X ", b) +
                ", ww=" + String.format("%02X ", ww) +
                ", cw=" + String.format("%02X ", cw) +
                "}";
    }

    /**
     * Generate commands for rgb channels and ww-cw channels and aggregate its in one command.
     *
     * @return command for set rgb-ww-cw channels.
     *
     * @throws IOException when error in interaction with {@link ByteArrayOutputStream}.
     */
    byte[] getChannelCommand() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(rgbCommand(r, g, b));
        baos.write(wwcwCommand(ww, cw));
        return baos.toByteArray();
    }

    /**
     * Prepare command to set rgb channels.
     *
     * @param r - red color (1 channel).
     * @param g - green color (2 channel).
     * @param b - blue color (3 channel).
     *
     * @return rgb command.
     */
    private static byte[] rgbCommand(byte r, byte g, byte b) {
        byte[] command = new byte[8];
        command[0] = (byte) 0x31;
        command[1] = r;
        command[2] = g;
        command[3] = b;
        command[4] = 0;
        command[5] = 0;
        command[6] = (byte) 0xf0;
        command[7] = (byte) 0x0f;
        return Utils.withCheckSum(command);
    }

    /**
     * Prepare command to set rgb channels.
     *
     * @param ww - warn white (4 channel).
     * @param cw - cold  white (5 channel).
     *
     * @return ww-cw command.
     */
    private static byte[] wwcwCommand(byte ww, byte cw) {
        byte[] command = new byte[8];
        command[0] = (byte) 0x31;
        command[1] = 0;
        command[2] = 0;
        command[3] = 0;
        command[4] = ww;
        command[5] = cw;
        command[6] = (byte) 0x0f;
        command[7] = (byte) 0x0f;
        return Utils.withCheckSum(command);
    }

}

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
     * @param r percent of red channel power.
     * @param g percent of green channel power.
     * @param b percent of blue channel power.
     * @param ww percent of warn white channel power.
     * @param cw percent of cold white channel power.
     */
    ControllerChannels(int r, int g, int b, int ww, int cw) {
        this.r = (byte) r;
        this.g = (byte) g;
        this.b = (byte) b;
        this.ww = (byte) ww;
        this.cw = (byte) cw;
    }

    /**
     * @return red power in percents.
     */
    public int getR() {
        return r & 0xFF;
    }

    /**
     * @return green power in percents.
     */
    public int getG() {
        return g & 0xFF;
    }

    /**
     * @return blue power in percents.
     */
    public int getB() {
        return b & 0xFF;
    }

    /**
     * @return warn white power in percents.
     */
    public int getWw() {
        return ww & 0xFF;
    }

    /**
     * @return cold white power in percents.
     */
    public int getCw() {
        return cw & 0xFF;
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
     * @return command for set rgb-ww-cw channels
     *
     * @throws IOException
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
     * @param r - red color (1 channel)
     * @param g - green color (2 channel)
     * @param b - blue color (3 channel)
     *
     * @return rgb command
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
     * Prepare command to set rgb channels
     *
     * @param ww - warn white (4 channel)
     * @param cw - cold  white (5 channel)
     *
     * @return ww-cw command
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

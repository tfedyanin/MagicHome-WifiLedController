package ru.ittim.openhab.ledbinding.library;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Channels state
 * It's possible to set only rgb state nor cw+ww state
 * Created by Timofey on 24.06.2016.
 */
class ControllerChannels {
    private final byte r;
    private final byte g;
    private final byte b;
    private final byte ww;
    private final byte cw;

    ControllerChannels(byte r, byte g, byte b, byte ww, byte cw) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.ww = ww;
        this.cw = cw;
    }

    public byte getR() {
        return r;
    }

    public byte getG() {
        return g;
    }

    public byte getB() {
        return b;
    }

    public byte getWw() {
        return ww;
    }

    public byte getCw() {
        return cw;
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
     * Prepare command to set rgb channels
     *
     * @param r - red color (1 channel)
     * @param g - green color (2 channel)
     * @param b - blue color (3 channel)
     * @return rgb command
     */
    private byte[] rgbCommand(byte r, byte g, byte b) {
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
     * @param ww - warn white (4 channel)
     * @param cw - cold  white (5 channel)
     * @return ww-cw command
     */
    private byte[] wwcwCommand(byte ww, byte cw) {
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

    /**
     * Generate commands for rgb channels and ww-cw channels and aggregate its in one commad
     * @return commad for set rgb-ww-cw channels
     * @throws IOException
     */
    byte[] getChannelCommand() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(rgbCommand(r, g, b));
        baos.write(wwcwCommand(ww, cw));
        return baos.toByteArray();
    }

}

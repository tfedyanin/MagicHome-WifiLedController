package ru.ittim.openhab.ledbinding;

/**
 * Created by Timofey on 24.06.2016.
 */
public class ControllerChannels {
    private final byte r;
    private final byte g;
    private final byte b;
    private final byte ww;
    private final byte cw;

    public ControllerChannels(byte r, byte g, byte b, byte ww, byte cw) {
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
}

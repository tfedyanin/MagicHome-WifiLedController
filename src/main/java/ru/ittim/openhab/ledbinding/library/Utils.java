package ru.ittim.openhab.ledbinding.library;

/**
 * Created by Timofey on 24.06.2016.
 */
public class Utils {
    public static byte[] withCheckSum(byte[] bytes) {
        byte[] res = new byte[bytes.length + 1];
        byte checkSum =0x0;
        for (int i = 0; i < bytes.length; i++) {
            res[i] = bytes[i];
            checkSum += bytes[i];
        }
        checkSum &= 0xFF;
        res[bytes.length] = checkSum;
        return res;
    }
}

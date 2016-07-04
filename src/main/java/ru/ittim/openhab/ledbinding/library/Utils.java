package ru.ittim.openhab.ledbinding.library;

/**
 * Helper for library
 * Created by Timofey on 24.06.2016.
 */
class Utils {
    /**
     * Calc checksum for command and return command with checksum in last byte
     * @param bytes command without checksum
     * @return command with checksum
     */
    static byte[] withCheckSum(byte[] bytes) {
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

package ru.ittim.openhab.ledbinding.library;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    public void withCheckSumTest(){
        byte[] initialArr = "Test".getBytes();
        byte[] bytes = Utils.withCheckSum(initialArr);
        initialArr = Arrays.copyOf(initialArr, initialArr.length + 1);
        initialArr[initialArr.length-1] = -96;
        assertArrayEquals(bytes, initialArr);
    }


}
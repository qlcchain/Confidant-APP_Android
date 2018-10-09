package com.stratagile.pnrouter.utils;

/**
 * Created by zl on 2018/10/9.
 */

public class Crc16 {

    private final int polynomial = 0x8408;

    private int[] table = new int[256];

    public int ComputeChecksum(int[] bytes) {
        int crc = 0xffff;
        for (int i = 0; i < bytes.length; ++i) {
            int index = (crc ^ bytes[i]) % 256;
            crc = (crc >> 8) ^ table[index];
        }
        return crc;
    }

    public Crc16() {
        int value;
        int temp;
        for (int i = 0; i < table.length; ++i) {
            value = 0;
            temp = i;
            for (byte j = 0; j < 8; ++j) {
                if (((value ^ temp) & 0x0001) != 0) {
                    value = (value >> 1) ^ polynomial;
                } else {
                    value >>= 1;
                }
                temp >>= 1;
            }
            table[i] = value;
        }
    }

    /*public static void main(String[] args) {
        Crc16 c = new Crc16();
        int[] arr = new int[]{0x4, 0x0, 0x1};
        System.out.println(Integer.toString(c.ComputeChecksum(arr), 16));
        arr = new int[]{0xB, 0x0, 0x1, 0x1, 0x1, 0x4, 0xEE, 0x35, 0x45, 0x45 };
        System.out.println(Integer.toString(c.ComputeChecksum(arr), 16));
    }*/
}

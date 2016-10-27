package com.example.liushuai.hcble.utils;

/**
 * Created by liushuai on 2016/9/13.
 */
public class Bytes {
    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public static String toBinaryString(byte b) {
        int u = toUnsigned(b);
        return new String(new char[]{
                DIGITS[(u >>> 7) & 0x1],
                DIGITS[(u >>> 6) & 0x1],
                DIGITS[(u >>> 5) & 0x1],
                DIGITS[(u >>> 4) & 0x1],
                DIGITS[(u >>> 3) & 0x1],
                DIGITS[(u >>> 2) & 0x1],
                DIGITS[(u >>> 1) & 0x1],
                DIGITS[u & 0x1]
        });
    }

    public static String toBinaryString(byte... bytes) {
        char[] buffer = new char[bytes.length * 8];
        for (int i = 0, j = 0; i < bytes.length; ++i) {
            int u = toUnsigned(bytes[i]);
            buffer[j++] = DIGITS[(u >>> 7) & 0x1];
            buffer[j++] = DIGITS[(u >>> 6) & 0x1];
            buffer[j++] = DIGITS[(u >>> 5) & 0x1];
            buffer[j++] = DIGITS[(u >>> 4) & 0x1];
            buffer[j++] = DIGITS[(u >>> 3) & 0x1];
            buffer[j++] = DIGITS[(u >>> 2) & 0x1];
            buffer[j++] = DIGITS[(u >>> 1) & 0x1];
            buffer[j++] = DIGITS[u & 0x1];
        }
        return new String(buffer);
    }

    public static String toHexString(byte b) {
        int u = toUnsigned(b);
        return new String(new char[]{
                DIGITS[u >>> 4],
                DIGITS[u & 0xf]
        });
    }

    public static String toHexString(byte... bytes) {
        char[] buffer = new char[bytes.length * 2];
        for (int i = 0, j = 0; i < bytes.length; ++i) {
            int u = toUnsigned(bytes[i]);
            buffer[j++] = DIGITS[u >>> 4];
            buffer[j++] = DIGITS[u & 0xf];
        }
        return new String(buffer);
    }

    private static int toUnsigned(byte b) {
        return b < 0 ? b + 256 : b;
    }
}

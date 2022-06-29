package com.dabomstew.pkrandom.hac;

public class FNVHash {
    private static final long kFNVPrime64 = 0x00000100000001B3L;
    private static final long kOffsetBasis_64 = 0xCBF29CE484222645L;

    public static long hashFNV164(byte [] input) {
        return hashFNV164(input, kOffsetBasis_64);
    }

    public static long hashFNV164(byte[] input, long hash) {
        for (byte b: input) {
            hash *= kFNVPrime64;
            hash ^= b;
        }
        return hash;
    }

    public static long hashFNV1a64(byte[] input) {
        return hashFNV1a64(input, kOffsetBasis_64);
    }

    public static long hashFNV1a64(byte[] input, long hash) {
        for (byte b: input) {
            hash ^= b;
            hash *= kFNVPrime64;
        }
        return hash;
    }
}

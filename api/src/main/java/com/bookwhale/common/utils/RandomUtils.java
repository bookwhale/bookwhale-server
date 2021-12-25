package com.bookwhale.common.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RandomUtils {
    // https://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string
    public static String createRandomString() {
        SecureRandom random = new SecureRandom();
        byte[] array = new byte[16];
        random.nextBytes(array);
        return new BigInteger(1, array).toString(16);
    }
}

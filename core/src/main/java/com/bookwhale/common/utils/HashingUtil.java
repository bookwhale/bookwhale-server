package com.bookwhale.common.utils;

import com.bookwhale.common.exception.CustomException;
import com.bookwhale.common.exception.ErrorCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HashingUtil {

    public static String sha256(String str) {
        return hashingFromGuava(str, "sha-256");
    }

    private static String hashingFromMessageDigest(String str, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.update(str.getBytes());
            return String.format("%040x", new BigInteger(1, messageDigest.digest()));
        } catch (NoSuchAlgorithmException e) {
            log.error("hashing 처리 오류 ({})", algorithm, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 'com.google.common.hash.Hashing' is marked unstable with @Beta
    private static String hashingFromGuava(String str, String algorithm) {
        try {
            HashFunction hashFunction = Hashing.sha256();
            if ("SHA-384".equalsIgnoreCase(algorithm)) {
                hashFunction = Hashing.sha384();
            } else if ("SHA-512".equalsIgnoreCase(algorithm)) {
                hashFunction = Hashing.sha512();
            }
            return hashFunction.hashString(str, StandardCharsets.UTF_8)
                .toString();
        } catch (Exception e) {
            log.error("hashing 처리 오류 ({})", algorithm, e);
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}

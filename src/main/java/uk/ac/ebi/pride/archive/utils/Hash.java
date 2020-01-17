package uk.ac.ebi.pride.archive.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    private final static Logger LOGGER = LoggerFactory.getLogger(Hash.class);

    public static MessageDigest getMD5() {
        return getHashingAlgorithm("MD5");
    }

    private static MessageDigest getHashingAlgorithm(final String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error in getting hash algorithm - {}", e.getMessage());
            throw new AssertionError(e);
        }
    }

    public static String normalize(MessageDigest messageDigest) {
        if ("MD5".equals(messageDigest.getAlgorithm())) {
            return DatatypeConverter.printHexBinary(messageDigest.digest()).toLowerCase();
        }
        return new String(messageDigest.digest());
    }
}

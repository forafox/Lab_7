package database;

import database.UserData;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * @author Karabanov Andrey
 * @version 1.0
 * @date 04.04.2023 0:31
 */
public class DataEncryptor {
    final static String PEPPER_VALUE="3d2ga";
//    public String encryptStringSHA256(String str) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            byte[] encodedBytes = md.digest(str.getBytes(StandardCharsets.UTF_8));
//            StringBuilder encodedSB = new StringBuilder(new BigInteger(1, encodedBytes).toString());
//            encodedSB.append("=3d2ga");
//            encodedBytes = md.digest(encodedSB.toString().getBytes(StandardCharsets.UTF_8));
//            encodedSB = new StringBuilder(new BigInteger(1, encodedBytes).toString(16));
//
//            while (encodedSB.length() < 32) {
//                encodedSB.insert(0, "0");
//            }
//            return encodedSB.toString();
//        } catch (NoSuchAlgorithmException ex) {
//            System.err.println("Нет заявленного алгоритма шифрования");
//        }
//        return null;
//    }

    public static String sha256(final String base, UserData userData,int firstTime) {
        try{
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            final StringBuilder hexString = new StringBuilder(PEPPER_VALUE);
            for (byte b : hash) {
                final String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            if(firstTime==1) {
                String salt=getRandomString();
            hexString.append(salt);
            userData.setSalt(salt);
                    }
            if(firstTime==2){
                String salt=userData.getSalt();
                hexString.append(salt);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    private static String getRandomString() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("-", "").substring(0,8);

    }
}


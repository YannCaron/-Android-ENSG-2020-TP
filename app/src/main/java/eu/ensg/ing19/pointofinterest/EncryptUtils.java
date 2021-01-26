package eu.ensg.ing19.pointofinterest;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtils implements Constants {

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String encrypt(String text) {
        try {
            MessageDigest encryptAlgo = MessageDigest.getInstance("SHA");

            String encodedPassword = bytesToHex(encryptAlgo.digest(text.getBytes()));

            Log.i(LOG_TAG, String.format("Encrypted password is: [%s]", encodedPassword));

            return encodedPassword;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}

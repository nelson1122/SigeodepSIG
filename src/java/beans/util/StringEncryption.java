package beans.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The StringEncryption class uses a series of algorithms for encryption.
 *
 */
public class StringEncryption {
    //algoritmos

    public static String MD2 = "MD2";
    public static String MD5 = "MD5";
    public static String SHA1 = "SHA-1";
    public static String SHA256 = "SHA-256";
    public static String SHA384 = "SHA-384";
    public static String SHA512 = "SHA-512";

    public StringEncryption() {
    }

    /**
     * Converts a byte array to String using hexadecimal values.
     *
     * @param digest: array byte to convert
     * @return String: created from <code>digest</code>
     */
    private static String toHexadecimal(byte[] digest) {
        String hash = "";
        for (byte aux : digest) {
            int b = aux & 0xff;
            if (Integer.toHexString(b).length() == 1) {
                hash += "0";
            }
            hash += Integer.toHexString(b);
        }
        return hash;
    }

    /**
     * Encrypts a text message using algorithm of summary of message .
     *
     * @param message: text to encrypt.
     * @param algorithm: encryption algorithm, can be: MD2, MD5, SHA-1, SHA-256,
     * SHA-384, SHA-512.
     * @return
     */
    public String getStringMessageDigest(String message, String algorithm) {
        byte[] digest = null;
        byte[] buffer = message.getBytes();
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.reset();
            messageDigest.update(buffer);
            digest = messageDigest.digest();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Error creando Digest");
        }
        return toHexadecimal(digest);
    }
}
package org.magmafoundation.magma.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5Checksum
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 20/11/2021 - 03:02 pm
 */
public class MD5Checksum {

    // Check MD5
    public static boolean checkMD5(File file, String md5sum) throws IOException {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("[ERROR] MD5 algorithm not found");
            return false;
        }

        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[1024];
        int read;
        while ((read = fis.read(data)) != -1) {
            digest.update(data, 0, read);
        }
        fis.close();

        byte[] md5sumBytes = fromHexString(md5sum);
        byte[] bytes = digest.digest();

        for (int i = 0; i < md5sumBytes.length; i++) {
            if (md5sumBytes[i] != bytes[i]) {
                return false;
            }
        }

        return true;
    }

    // Convert Hex String to Byte Array
    private static byte[] fromHexString(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static byte[] createChecksum(String filename) throws Exception {
        InputStream fis =  new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    public static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i=0; i < b.length; i++) {
            result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
        }
        return result;
    }

}
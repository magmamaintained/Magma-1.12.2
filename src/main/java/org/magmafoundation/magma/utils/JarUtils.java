package org.magmafoundation.magma.utils;

import java.io.File;

/**
 * JarUtils
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 20/11/2021 - 01:32 pm
 */
public class JarUtils {

    public static String getJarPath() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return file.getAbsolutePath();
    }

    public static String getJarDir() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return getFile().getParent();
    }

    public static String getJarName() {
        File file = getFile();
        if (file == null) {
            return null;
        }
        return getFile().getName();
    }

    public static File getFile() {

        String path = JarUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return new File(path);
    }
}

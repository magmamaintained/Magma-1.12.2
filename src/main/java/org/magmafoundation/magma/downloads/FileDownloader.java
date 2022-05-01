package org.magmafoundation.magma.downloads;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * FileDownloader
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 01/05/2022 - 11:53 am
 */
public class FileDownloader {

    public static void downloadFile(String downloadLink, File fileName) {
        try {
            URL website = new URL(downloadLink);
            HttpURLConnection connection = (HttpURLConnection) website.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "Magma");
            ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

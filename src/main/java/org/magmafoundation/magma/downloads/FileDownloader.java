/*
 * Magma Server
 * Copyright (C) 2019-2022.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

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

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.fusesource.jansi.Ansi;
import org.magmafoundation.magma.Magma;

/**
 * MagmaUpdater
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 09/05/2020 - 03:51 pm
 */
public class MagmaUpdater {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String newSha;
    private String currentSha;
    private String versionURL = "https://api.github.com/repos/magmamaintained/Magma-1.12.2/releases/latest";
    private String assetURL;

    public boolean versionChecker() {
        try {
            URL url = new URL(versionURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "Magma");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonObject root = gson.fromJson(reader, JsonObject.class);

            newSha = root.get("tag_name").getAsString();
            assetURL = root.get("assets").getAsJsonArray().get(0).getAsJsonObject().get("browser_download_url").getAsString();
            currentSha = Magma.class.getPackage().getImplementationVersion().split("-")[1];

            if (currentSha.equals(newSha)) {
                System.out.printf("[Magma] No update found, latest version: (%s) current version: (%s)%n", currentSha, newSha);
                return false;
            } else {
                System.out.printf("[Magma] The latest Magma version is (%s) but you have (%s).%n", newSha, currentSha);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void downloadJar() {
        try {
            Path path = Paths.get(MagmaUpdater.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            System.out.println("[Magma] Updating Magma Jar ...");
            System.out.println("[Magma] Downloading " + assetURL + " ...");
            URL website = new URL(assetURL);
            HttpURLConnection connection = (HttpURLConnection) website.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("User-Agent", "Magma");
            ReadableByteChannel rbc = Channels.newChannel(connection.getInputStream());
            FileOutputStream fos = new FileOutputStream(path.toFile());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException | URISyntaxException e) {
            System.out.println("[Magma] Failed to download update! Starting old version.");
            return;
        }
        System.out.println("[Magma] Download Complete! Please restart the server.");
        System.exit(0);
    }

}

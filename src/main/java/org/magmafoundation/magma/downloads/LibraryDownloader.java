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

import javax.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import org.fusesource.jansi.Ansi;
import org.magmafoundation.magma.utils.JarLoader;
import org.magmafoundation.magma.utils.MD5Checksum;

/**
 * LibraryDownloader
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 28/07/2020 - 06:05 pm
 */
public class LibraryDownloader {

    public void run() {
        System.out.println("[Magma] Starting Library Downloader");
        JsonArray libs = parseLibraryJson(new InputStreamReader(getClass().getResourceAsStream("/magma_libs.json")));
        libs.forEach(jsonElement -> {
            String name = jsonElement.getAsJsonObject().get("name").getAsString();
            String repo = jsonElement.getAsJsonObject().get("repo").getAsString();
            String md5 = jsonElement.getAsJsonObject().get("md5").getAsString();

            boolean md5_skip = false;
            if(jsonElement.getAsJsonObject().get("md5_skip") != null) {
                md5_skip = jsonElement.getAsJsonObject().get("md5_skip").getAsBoolean();
            }

            Libary libary = craftLibary(name, repo, md5, md5_skip);
            try {
                File file = downloadFile(libary);
                JarLoader.addFile(file);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private JsonArray parseLibraryJson(Reader reader) {
        Gson gson = new GsonBuilder().create();

        JsonElement librarys = gson.fromJson(reader, JsonElement.class);
        String verison = librarys.getAsJsonObject().get("version").getAsString();
        System.out.println("[Magma] Library Version: " + verison);
        JsonArray libs = librarys.getAsJsonObject().get("libraries").getAsJsonArray();
        return libs;
    }

    private Libary craftLibary(String fullName, String repo, String md5, boolean md5_skip) {
        String groupId = fullName.split(":")[0];
        groupId = groupId.replaceAll("\\.", "/"); // Replaces all '.' with '/'
        String artifactId = fullName.split(":")[1];
        String version = fullName.split(":")[2];
        try {
            String time = fullName.split(":")[3]; // Support for Timed versions
            if (time != null) {
                String jarName = artifactId + "-" + version.replace("SNAPSHOT", "") + time + ".jar";
                String folderName = groupId + "/" + artifactId + "/" + version;
                String url = repo + "/" + folderName + "/" + jarName;
                Libary libary = new Libary(jarName, folderName, url, md5, md5_skip);
                return libary;
            }
        } catch (Exception e) {
        }

        String jarName = artifactId + "-" + version + ".jar";
        String folderName = groupId + "/" + artifactId + "/" + version;
        String url = repo + "/" + folderName + "/" + jarName;

        Libary libary = new Libary(jarName, folderName, url, md5, md5_skip);
        return libary;
    }

    private class Libary {

        private final String jarName;
        private final String folderName;
        private final String url;
        private final String md5sum;
        private final boolean md5_skip;

        public Libary(String jarName, String folderName, String url, String md5sum, boolean md5_skip) {
            this.jarName = jarName;
            this.folderName = folderName;
            this.url = url;
            this.md5sum = md5sum;
            this.md5_skip = md5_skip;
        }

        @Override
        public String toString() {
            return "Libary{" +
                    "jarName='" + jarName + '\'' +
                    ", folderName='" + folderName + '\'' +
                    ", url='" + url + '\'' +
                    ", md5sum='" + md5sum + '\'' +
                    ", md5_skip=" + md5_skip +
                    '}';
        }
    }

    private File downloadFile(Libary libary) throws Exception {
        String folderName = "./libraries/" + libary.folderName + "/";
        String fullPath = folderName + libary.jarName;

        if (new File(fullPath).exists()) {
            if (libary.md5_skip) {
                return new File(fullPath);
            }
            if (MD5Checksum.getMD5Checksum(fullPath).equals(libary.md5sum)) {
                return new File(fullPath);
            } else {
                new File(folderName).mkdirs();
                System.out.println("[Magma] MD5 is Different Re Downloading Jar: " + fullPath);

                File file = new File(fullPath);
                FileDownloader.downloadFile(libary.url, file);
                return file;
            }
        } else {
            new File(folderName).mkdirs();

            System.out.println("[Magma] Downloading Jar: " + fullPath);

            File file = new File(fullPath);
            FileDownloader.downloadFile(libary.url, file);
            return file;
        }
    }
}

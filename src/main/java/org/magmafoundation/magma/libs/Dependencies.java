package org.magmafoundation.magma.libs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.magmafoundation.magma.MagmaConstants;
import org.magmafoundation.magma.utils.JarLoader;
import org.magmafoundation.magma.utils.JarUtils;
import org.magmafoundation.magma.utils.MD5Checksum;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;
import java.util.stream.StreamSupport;

public class Dependencies {

    private static final File log = new File("logs/installer.log");
    private static FileWriter logWriter;
    private static final File libraries_folder = new File(String.format("%s/%s/", JarUtils.getJarDir(), MagmaConstants.INSTALLER_LIBRARIES_FOLDER));
    private static File serverJar = new File(libraries_folder, "minecraft_server.1.12.2.jar");

    private static JsonArray libraries;

    public static void start() throws IOException {
        if (log.exists())
            log.delete();
        log.getParentFile().mkdirs();
        log.createNewFile();
        logWriter = new FileWriter(log, true);

        System.out.println("Starting dependency checker...");

        checkServerJar();

        libraries = parseLibraryJson(new InputStreamReader(Dependencies.class.getResourceAsStream("/magma_libs.json")));

        ProgressBarBuilder builder = new ProgressBarBuilder()
                .setTaskName("Checking libraries...")
                .setInitialMax(libraries.size())
                .setUpdateIntervalMillis(100)
                .setStyle(ProgressBarStyle.ASCII);

        ProgressBar.wrap(StreamSupport.stream(libraries.spliterator(), true).parallel(), builder).forEach(jsonElement -> {
            String name = jsonElement.getAsJsonObject().get("name").getAsString();
            String repo = jsonElement.getAsJsonObject().get("repo").getAsString();
            String md5 = jsonElement.getAsJsonObject().get("md5").getAsString();
            String ext = null;

            boolean md5_skip = false;
            if (jsonElement.getAsJsonObject().get("md5_skip") != null)
                md5_skip = jsonElement.getAsJsonObject().get("md5_skip").getAsBoolean();

            if (jsonElement.getAsJsonObject().get("ext") != null)
                ext = jsonElement.getAsJsonObject().get("ext").getAsString();

            Library library = craftLibrary(name, repo, md5, ext, md5_skip);
            try {
                File file = downloadCraftLibrary(library);
                JarLoader.addFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        logWriter.close();
    }

    private static File downloadCraftLibrary(Library lib) throws Exception {
        String folderName = String.format("%s/%s/", libraries_folder, lib.folderName);
        String fullPath = folderName + lib.jarName;

        File folder = new File(folderName);
        File file = new File(fullPath);

        if (file.exists()) {
            if (lib.md5_skip) {
                return file;
            }
            if (MD5Checksum.getMD5Checksum(fullPath).equals(lib.md5sum)) {
                return file;
            } else {
                folder.mkdirs();
                logWriter.write("The downloaded file '" + file.getName() + "' has an invalid MD5 checksum. Redownloading...");
                return downloadDependency(lib.url, file, lib.md5sum, 0);
            }
        } else {
            folder.mkdirs();
            return downloadDependency(lib.url, file, lib.md5_skip ? "-1" : lib.md5sum, 0);
        }
    }

    private static File downloadDependency(String location, File file, String checksum, int t) throws IOException {
        if (!file.exists() && !file.isDirectory()) {
            try {
                logWriter.write(String.format("%s%n", "Downloading Jar: " + file.getName()));
                file.getParentFile().mkdirs();
                file.createNewFile();
                URL url = new URL(location);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", MagmaConstants.NAME);

                try (ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream())) {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (checksum.equals("-1"))
            return file;

        if (!MD5Checksum.checkMD5(file, checksum)) {
            if (t < 3) {
                logWriter.write("The downloaded file '" + file.getName() + "' has an invalid MD5 checksum. Redownloading... Try (" + ++t + "/3)");
                file.delete();
                downloadDependency(location, file, checksum, t);
            } else {
                System.out.println();
                System.err.println("Failed to download file '" + file.getName() + "' after 3 tries. Aborting...");
                logWriter.close();
                System.exit(-1);
            }
        }
        return file;
    }

    private static File downloadDependency(String location, String filename, String checksum) throws IOException {
        return downloadDependency(location, new File(filename), checksum, 0);
    }

    private static void checkServerJar() throws IOException {
        String downloadLink = "https://launcher.mojang.com/v1/objects/886945bfb2b978778c3a0288fd7fab09d315b25f/server.jar";
        String expectedMD5Hash = "71728ed3fbd0acd1394bf3ade2649a5c";

        File[] serverLibs = new File[] {
                downloadDependency("https://repo1.maven.org/maven2/org/jline/jline/3.21.0/jline-3.21.0.jar", libraries_folder + "/org/jline/jline/3.21.0/jline-3.21.0.jar", "859778f9cdd3bd42bbaaf0f6f7fe5e6a"),
                downloadDependency("https://repo1.maven.org/maven2/me/tongfei/progressbar/0.9.2/progressbar-0.9.2.jar", libraries_folder + "/me/tongfei/progressbar/0.9.2/progressbar-0.9.2.jar", "5563c8a0525c5a40f340d23ea63be2b5"),
                downloadDependency("https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.7/gson-2.8.7.jar", libraries_folder + "/com/google/code/gson/gson/2.8.7/gson-2.8.7.jar", "87b5c368aecef232d401f4a159e7b9af")
        };

        Arrays.stream(serverLibs).parallel().forEach(file -> {
            if (file.exists()) {
                if (file.getName().endsWith(".jar")) {
                    try {
                        JarLoader.addFile(file);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        File oldServerJar = new File("minecraft_server.1.12.2.jar");
        if (oldServerJar.exists())
            oldServerJar.delete();

        if (serverJar.exists()) {
            if (MD5Checksum.checkMD5(serverJar, expectedMD5Hash)) {
                // Load the server jar
                JarLoader.addFile(serverJar);
                return;
            } else {
                System.out.println("Server jar checksum failed, redownloading...");
                serverJar = downloadDependency(downloadLink, serverJar.getPath(), expectedMD5Hash);
            }
        }

        if (!serverJar.exists()) {
            System.out.println("Server jar not found, downloading...");
            serverJar = downloadDependency(downloadLink, serverJar.getPath(), expectedMD5Hash);
        }

        // Load the server jar
        JarLoader.addFile(serverJar);
    }

    private static JsonArray parseLibraryJson(Reader reader) {
        Gson gson = new GsonBuilder().create();
        JsonElement libraries = gson.fromJson(reader, JsonElement.class);
        String version = libraries.getAsJsonObject().get("version").getAsString();
        System.out.println("Library Version: " + version);
        return libraries.getAsJsonObject().get("libraries").getAsJsonArray();
    }

    public static JsonArray getLibraries() {
        return libraries;
    }

    private static Library craftLibrary(String fullName, String repo, String md5, String extension, boolean md5_skip) {
        //split the full artifact name by ':'
        String[] name = fullName.split(":");

        String libraryName = name[0].replace(".", "/");
        String artifactId = name[1];
        String version = name[2];
        String ext = extension == null ? "jar" : extension;

        String jarName;

        if (name.length == 4)
            jarName = artifactId + "-" + version.replace("SNAPSHOT", "") + name[3] + "." + ext;
        else
            jarName = artifactId + "-" + version + "." + ext;

        String folder = String.format("%s/%s/%s", libraryName, artifactId, version);
        String url = String.format("%s/%s/%s", repo, folder, jarName);

        return new Library(jarName, folder, url, md5, md5_skip);
    }

    private static class Library {

        private final String jarName;
        private final String folderName;
        private final String url;
        private final String md5sum;
        private final boolean md5_skip;

        public Library(String jarName, String folderName, String url, String md5sum, boolean md5_skip) {
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
}

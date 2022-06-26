package org.magmafoundation.magma.betterui;

import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BetterUI {

    private static boolean enabled = true,  enableBigLogo = true;

    private static final String bigLogo =
                    "    __  ___                           \n" +
                    "   /  |/  /___ _____ _____ ___  ____ _\n" +
                    "  / /|_/ / __ `/ __ `/ __ `__ \\/ __ `/\n" +
                    " / /  / / /_/ / /_/ / / / / / / /_/ / \n" +
                    "/_/  /_/\\__,_/\\__, /_/ /_/ /_/\\__,_/  \n" +
                    "             /____/                   ";

    public static void printTitle(String name, String brand, String javaVersion, String version, String bukkitVersion, String forgeVersion) {
        if (!enabled)
            return;
        if (enableBigLogo)
            System.out.println(bigLogo);
        else System.out.println(name);
        System.out.println("Copyright (c) " + new SimpleDateFormat("yyyy").format(new Date()) + " " + brand + ".");
        System.out.println("--------------------------------------");
        System.out.println("Running on Java " + javaVersion);
        System.out.println(name + " version  " + version);
        System.out.println("Bukkit version " + bukkitVersion);
        System.out.println("Forge version  " + forgeVersion);
        System.out.println("--------------------------------------");
    }

    public static boolean checkEula(Path path_to_eula) throws IOException {
        File file = path_to_eula.toFile();
        ServerEula eula = new ServerEula(path_to_eula);

        if (!enabled)
            return eula.hasAgreedToEULA();

        if (!eula.hasAgreedToEULA()) {
            System.out.println("WARNING: It appears you have not agreed to the EULA.\nPlease read the EULA (https://account.mojang.com/documents/minecraft_eula) and type 'yes' to continue.");
            System.out.print("Do you accept? (yes/no): ");

            int wrong = 0;

            while (true) {
                BufferedReader input = new BufferedReader( new InputStreamReader( System.in ) );
                String answer = input.readLine();
                switch (answer.toLowerCase()) {
                    case "yes":
                        file.delete();
                        file.createNewFile();
                        try (FileWriter writer = new FileWriter(file)) {
                            writer.write("eula=true");
                        }
                        return true;
                    case "no":
                        System.err.println("You must accept the EULA to continue. Exiting.");
                        return false;
                    default:
                        if (wrong++ >= 2) {
                            System.err.println("You have typed the wrong answer too many times. Exiting.");
                            return false;
                        }
                        System.out.println("Please type 'yes' or 'no'.");
                        System.out.print("Do you accept? (yes/no): ");
                        break;
                }
            }
        } else return true;
    }

    public static void setEnabled(boolean enabled) {
        BetterUI.enabled = enabled;
    }

    public static void setEnableBigLogo(boolean enableBigLogo) {
        BetterUI.enableBigLogo = enableBigLogo;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}
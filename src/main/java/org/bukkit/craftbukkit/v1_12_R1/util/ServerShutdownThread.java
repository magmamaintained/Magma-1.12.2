package org.bukkit.craftbukkit.v1_12_R1.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;

public class ServerShutdownThread extends Thread {
    private final MinecraftServer server;

    public ServerShutdownThread(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            org.spigotmc.AsyncCatcher.enabled = false; // Spigot
            org.spigotmc.AsyncCatcher.shuttingDown = true; // Paper
            server.stopServer();
        } catch (MinecraftException ex) {
            ex.printStackTrace();
        } finally {
            try {
                net.minecrell.terminalconsole.TerminalConsoleAppender.close(); // Paper - Use TerminalConsoleAppender
            } catch (Exception e) {
            }
        }
    }
}

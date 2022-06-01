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

package org.magmafoundation.magma.api;

import io.netty.util.internal.ConcurrentSet;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.inventory.ItemStack;

/**
 * ServerAPI
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 19/08/2019 - 04:47 pm
 */
public class ServerAPI {

    public static Map<String, Integer> mods = new ConcurrentHashMap<>();
    public static Set<String> modList = new ConcurrentSet<>();
    public static Map<String, String> commands = new ConcurrentHashMap<>();

    /**
     * How many mods are present if the user didn't add any mods
     */
    public static final int DEFAULT_MODS = 5;

    /**
     * How many mods are loaded.
     *
     * @deprecated Use {@link ServerAPI#getModSize(boolean)} instead
     * @return int - loaded mods.
     */
    public static int getModSize() {
        return getModSize(false);
    }

    // This method has been inspired by Mohist
    /**
     * How many mods are loaded.
     *
     * @param onlyRealMods whether mods like fml/forge/... should not be counted
     * @return int - loaded mods.
     */
    public static int getModSize(boolean onlyRealMods) {
        return mods.get("mods") == null ? 0 : mods.get("mods") - (onlyRealMods ? DEFAULT_MODS : 0);
    }

    /**
     * List all loaded mods by name.
     *
     * @return String - List of mods.
     */
    public static String getModList() {
        return modList.toString();
    }

    /**
     * Checks if a mod is in the list.
     *
     * @param modid for the mod to check.
     * @return boolean - if it's in the list or not.
     */
    public static boolean hasMod(String modid) {
        return getModList().contains(modid);
    }

    // This method has been taken from Mohist
    /**
     * Checks if a plugin is loaded.
     *
     * @param pluginname name of the plugin to check.
     * @return boolean - if it's loaded or not.
     */
    public static Boolean hasPlugin(String pluginname) {
        return Bukkit.getPluginManager().getPlugin(pluginname) != null;
    }

    /**
     * Gets the Minecraft Server instance.
     *
     * @return MinecraftServer instance.
     */
    public static MinecraftServer getNMSServer() {
        return MinecraftServer.getServerInstance();
    }

    /**
     * Gets the CraftBukkit Server
     *
     * @return CraftServer instance.
     */
    public static CraftServer getCBServer() {
        return (CraftServer) Bukkit.getServer();
    }

    /**
     * Get oredict entries as a list of {@link ItemStack}s for a key
     *
     * @deprecated Use {@link OreDictAPI#getOreDictItems(String)} instead
     * @param key Name of the oredict entry
     * @return List of {@link ItemStack}s
     */
    @Deprecated
    public static List<ItemStack> getOreDictItems(String key) {
        return OreDictAPI.getOreDictItems(key);
    }

    // This method has been taken from Mohist
    /**
     * Gets the Main NMS World
     *
     * @return NMS World
     */
    public static World getMainWorld(){
        return getNMSServer().getEntityWorld();
    }

}

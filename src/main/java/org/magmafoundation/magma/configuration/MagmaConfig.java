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

package org.magmafoundation.magma.configuration;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.magmafoundation.magma.commands.MagmaCommand;
import org.magmafoundation.magma.commands.TPSCommand;
import org.magmafoundation.magma.commands.VersionCommand;
import org.magmafoundation.magma.configuration.value.Value;
import org.magmafoundation.magma.configuration.value.values.BooleanValue;
import org.magmafoundation.magma.configuration.value.values.IntValue;
import org.magmafoundation.magma.configuration.value.values.StringArrayValue;
import org.magmafoundation.magma.configuration.value.values.StringValue;

/**
 * MagmaConfig
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 19/08/2019 - 05:14 am
 */
public class MagmaConfig extends ConfigBase {

    public static MagmaConfig instance = new MagmaConfig();
    public static final int CONFIG_VERSION = 2;

    // DEBUG
    public final BooleanValue debugPrintBukkitMaterials = new BooleanValue(this, "debug.print-bukkit-materials", false, "Prints the Forge Bukkit Materials");
    public final BooleanValue debugPrintBukkitBannerPatterns = new BooleanValue(this, "debug.print-bukkit-banner-patterns", false, "Prints the Forge Bukkit Banner Patterns");
    public final BooleanValue debugPrintCommandNode = new BooleanValue(this, "debug.print-command-node", false, "Prints out all Command Nodes for permissions");
    public final BooleanValue debugPrintBiomes = new BooleanValue(this, "debug.print-biome-names", false, "Prints out all registered biomes");
    public final BooleanValue debugPrintSounds = new BooleanValue(this, "debug.print-sound-names", false, "Prints out all registered sounds");

    // BLACKLISTED MODS
    public final BooleanValue blacklistedModsEnable = new BooleanValue(this, "magma.mod-blacklist.enabled", false, "Enable blacklisting of mods");
    public final StringArrayValue blacklistedMods = new StringArrayValue(this, "magma.mod-blacklist.list", "xray", "A list of mods to blacklist");

    // WORLD AND DIMENSION SETTINGS
    public final IntValue expMergeMaxValue = new IntValue(this, "world.experience-merge-max-value", -1, "Instructs the server put a maximum value on experience orbs, preventing them all from merging down into 1 single orb.");
    public final BooleanValue enableAutoUnloadingDimensions = new BooleanValue(this, "world.dimensions.auto-unload-dimensions", true, "Automatically unload dimensions that are not being used");
    public List<Integer> autoUnloadDimensionsWhitelist = Lists.newArrayList(13371337);
    public final BooleanValue respawnInOtherDim = new BooleanValue(this, "world.dimensions.respawn-in-other-dim", true, "Allows players to respawn in other dimensions");
    public final BooleanValue allowBlockLoadChunk = new BooleanValue(this, "world.loading.allow-block-load-chunk", true, "Allow blocks and tile entities to load chunks");
    public final BooleanValue forceUnloadChunks = new BooleanValue(this, "world.loading.force-unload-chunks", false, "Force unloads the chunk despite the fact that minecraft marked the unloading of the chunk as canceled");

    // FAKEPLAYER
    public final StringArrayValue fakePlayerPermissions = new StringArrayValue(this, "magma.advanced.fakeplayer.permissions", "example.test", "A list of permissions that fake players should have");

    // SERVER NAME
    public final BooleanValue overrideServerName = new BooleanValue(this, "magma.advanced.override-name", false, "Enable overriding the name string");
    public final StringValue serverName = new StringValue(this, "magma.advanced.override-name-string", "Spigot", "Value to use for the new name string");
    public final BooleanValue overrideServerBrand = new BooleanValue(this, "magma.advanced.override-brand", false, "Enables overriding the brand string");
    public final StringValue serverBrand = new StringValue(this, "magma.advanced.override-brand-string", "SpigotMC", "Value to use for new brand string");
    public final StringValue serverBrandType = new StringValue(this, "magma.advanced.server-type", "FML", "Set to FML to show forge icon or BUKKIT to show bukkit icon (FML is default)");

    // OTHER
    public final BooleanValue forgeBukkitPermissionHandlerEnable = new BooleanValue(this, "magma.advanced.enable-bukkit-permission-handler", true, "Let's Bukkit permission plugins handle forge/modded commands");
    public final BooleanValue forgeBukkitAccess = new BooleanValue(this, "magma.advanced.forge-bukkit-access", true, "Allows Forge mods to access Bukkit plugin classes");
    public final StringValue toolTipOverridePriority = new StringValue(this, "magma.advanced.tooltip-priority", "mod", "Mod, Plugin, None : determines what has tooltip priority");
    public final BooleanValue magmaAutoUpdater = new BooleanValue(this, "magma.update.auto-update", true, "Auto updates the Magma jar (Requires check-for-updates to be enabled)");
    public final BooleanValue checkForUpdates = new BooleanValue(this, "magma.update.check-for-updates", true, "Check for updates on startup");

    // BUKKIT/SPIGOT/TACO/...
    public final IntValue maxPotionEffectAmount = new IntValue(this, "bukkit.max-potion-effect-amount", 1024, "Maximum amount of possible potion effects (Bukkit's default is 300)");
    public final BooleanValue enableReloadCommand = new BooleanValue(this, "bukkit.enable-reload", false, "Enables the reload command (not recommended)");
    public final BooleanValue tacoFixNoDamageTicks = new BooleanValue(this, "bukkit.taco.fix-no-damage-ticks", false, "Applies Taco's fix for getNoDamageTicks");
    public final BooleanValue tacoFireArrowCollideEvent = new BooleanValue(this, "bukkit.taco.fire-arrow-collide-event", true, "Toggles the triggering of Taco's ArrowCollideEvent");
    public final BooleanValue tacoBetterPvP = new BooleanValue(this, "bukkit.taco.better-pvp", false, "Applies Taco's fix for better PvP");

    // MESSAGES
    public final StringValue fmlRequiredMessage = new StringValue(this, "magma.messages.fml.fml-required", "&cThis Server is running Magma. Forge and additional mods are required in order to connect to this server.", "FML required kick message");
    public final StringValue missingModsMessage = new StringValue(this, "magma.messages.fml.missing-mods", "&cYou are missing the following mods:", "Missing Mods kick message");
    public final StringValue serverStillStartingMessage = new StringValue(this, "magma.messages.fml.server-still-starting", "&cServer is still starting! Please wait before reconnecting.", "Server still starting kick message");
    public final StringValue blacklistedModsKickMessage = new StringValue(this, "magma.messages.blacklist-kick-message", "Please Remove Blacklisted Mods", "Mod Blacklist kick message");

    public static final String HEADER = "This is the main configuration file for Magma.\n" +
            "\n" +
            "Site: https://magmafoundation.org\n" +
            "Discord: https://discord.gg/magma\n";


    public MagmaConfig() {
        super("magma.yml", "magma");
        init();
    }

    public static String getString(String s, String key, String defaultreturn) {
        if (s.contains(key)) {
            String string = s.substring(s.indexOf(key));
            String s1 = (string.substring(string.indexOf(": ") + 2));
            String[] ss = s1.split("\n");
            return ss[0].trim().replace("'", "").replace("\"", "");
        }
        return defaultreturn;
    }

    public static String getString(File f, String key, String defaultreturn) {
        try {
            String s = FileUtils.readFileToString(f, "UTF-8");
            if (s.contains(key)) {
                String string = s.substring(s.indexOf(key));
                String s1 = (string.substring(string.indexOf(": ") + 2));
                String[] ss = s1.split("\n");
                return ss[0].trim().replace("'", "").replace("\"", "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return defaultreturn;
    }

    public void init() {
        for (Field f : this.getClass().getFields()) {
            if (Modifier.isFinal(f.getModifiers()) && Modifier.isPublic(f.getModifiers()) && !Modifier.isStatic(f.getModifiers())) {
                try {
                    Value value = (Value) f.get(this);
                    if (value == null) {
                        continue;
                    }
                    values.put(value.path, value);
                } catch (ClassCastException e) {
                } catch (Throwable t) {
                    System.out.println("[Magma] Failed to initialize a MagmaConfig values.");
                    t.printStackTrace();
                }
            }
        }
        load();
    }

    @Override
    protected void addCommands() {
        commands.put("magma", new MagmaCommand("magma"));
        commands.put("tps", new TPSCommand("tps"));
        commands.put("version", new VersionCommand("version"));
    }

    @Override
    protected void load() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            StringBuilder header = new StringBuilder(HEADER + "\n");
            for (Value toggle : values.values()) {
                if (!toggle.description.equals("")) {
                    header.append("Value: ").append(toggle.path).append(" Default: ").append(toggle.key).append("   # ").append(toggle.description).append("\n");
                }

                config.addDefault(toggle.path, toggle.key);
                values.get(toggle.path).setValues(config.getString(toggle.path));
            }
            version = getInt("config-version", CONFIG_VERSION);
            set("config-version", CONFIG_VERSION);

            config.addDefault("world.dimensions.auto-unload-whitelist", new int[]{0});
            this.autoUnloadDimensionsWhitelist = config.getIntegerList("world.dimensions.auto-unload-whitelist");

            config.options().header(header.toString());
            config.options().copyDefaults(true);

            this.save();
        } catch (Exception ex) {
            MinecraftServer.getServerInstance().server.getLogger()
                    .log(Level.SEVERE, "Could not load " + this.configFile);
            ex.printStackTrace();
        }
    }
}

package org.magmafoundation.magma.configuration;

import net.minecraft.server.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.YamlConfiguration;
import org.magmafoundation.magma.configuration.value.Value;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * ConfigBase
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 19/08/2019 - 05:17 am
 */
public abstract class ConfigBase {
    protected final File configFile;
    protected final String commandName;

    /* ======================================================================== */

    public YamlConfiguration config;
    protected int version;
    protected Map<String, Command> commands;
    protected Map<String, Value> values = new HashMap<String, Value>();

    /* ======================================================================== */

    public ConfigBase(String fileName, String commandName) {
        this.configFile = new File(fileName);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.commandName = commandName;
        this.commands = new HashMap<>();
        this.addCommands();
    }

    private static String transform(String s) {
        return ChatColor.translateAlternateColorCodes('&', s).replaceAll("\\\\n", "\n");
    }

    protected abstract void addCommands();

    public Map<String, Value> getSettings() {
        return values;
    }

    public void registerCommands() {
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            MinecraftServer.getServerCB().server.getCommandMap().register(entry.getKey(), this.commandName, entry.getValue());
        }
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException ex) {
            MinecraftServer.getServerCB().server.getLogger().log(Level.SEVERE, "Could not save " + configFile);
            ex.printStackTrace();
        }
    }

    protected abstract void load();

    public void set(String path, Object val) {
        config.set(path, val);
    }

    public boolean isSet(String path) {
        return config.isSet(path);
    }

    public boolean isInt(String path) {
        return config.isInt(path);
    }

    public boolean isBoolean(String path) {
        return config.isBoolean(path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean def) {
        return getBoolean(path, def, true);
    }

    public boolean getBoolean(String path, boolean def, boolean useDefault) {
        if (useDefault) {
            config.addDefault(path, def);
        }
        return config.getBoolean(path, def);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private <T> List getList(String path, T def) {
        config.addDefault(path, def);
        return config.getList(path, config.getList(path));
    }

    public String getString(String path, String def) {
        return getString(path, def, true);
    }

    public String getString(String path, String def, boolean useDefault) {
        if (useDefault) {
            config.addDefault(path, def);
        }
        return config.getString(path, def);
    }

    public String getFakePlayer(String className, String defaultName) {
        return getString("fake-players." + className + ".username", defaultName);
    }
}

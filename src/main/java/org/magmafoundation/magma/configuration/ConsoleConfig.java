package org.magmafoundation.magma.configuration;

import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.magmafoundation.magma.commands.MagmaCommand;
import org.magmafoundation.magma.commands.TPSCommand;
import org.magmafoundation.magma.commands.VersionCommand;
import org.magmafoundation.magma.configuration.ConfigBase;
import org.magmafoundation.magma.configuration.value.Value;
import org.magmafoundation.magma.configuration.value.values.StringValue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

public class ConsoleConfig extends ConfigBase {

    public static ConsoleConfig instance = new ConsoleConfig();
    public static final int CONFIG_VERSION = 1;

    public ConsoleConfig() {
        super("console.yml", "console");
        init();
    }

    //============================Console======================================

    public final StringValue highlightLevelError = new StringValue(this, "console.color.level.error", "c", "The color for the error level");
    public final StringValue highlightLevelWarning = new StringValue(this, "console.color.level.warning", "e", "The color for the warning level");
    public final StringValue highlightLevelInfo = new StringValue(this, "console.color.level.info", "2", "The color for the info level");
    public final StringValue highlightLevelFatal = new StringValue(this, "console.color.level.fatal", "e", "The color for the fatal level");
    public final StringValue highlightLevelTrace = new StringValue(this, "console.color.level.trace", "e", "The color for the trace level");

    public final StringValue highlightMessageError = new StringValue(this, "console.color.message.error", "c", "The color for the error message");
    public final StringValue highlightMessageWarning = new StringValue(this, "console.color.message.warning", "e", "The color for the warning message");
    public final StringValue highlightMessageInfo = new StringValue(this, "console.color.message.info", "2", "The color for the info message");
    public final StringValue highlightMessageFatal = new StringValue(this, "console.color.message.fatal", "e", "The color for the fatal message");
    public final StringValue highlightMessageTrace = new StringValue(this, "console.color.message.trace", "e", "The color for the trace message");

    public final StringValue highlightTimeError = new StringValue(this, "console.color.time.error", "c", "The color for the error time");
    public final StringValue highlightTimeWarning = new StringValue(this, "console.color.time.warning", "e", "The color for the warning time");
    public final StringValue highlightTimeInfo = new StringValue(this, "console.color.time.info", "2", "The color for the info time");
    public final StringValue highlightTimeFatal = new StringValue(this, "console.color.time.fatal", "e", "The color for the fatal time");
    public final StringValue highlightTimeTrace = new StringValue(this, "console.color.time.trace", "e", "The color for the trace time");

    public ConsoleConfig(String fileName, String commandName) {
        super(fileName, commandName);
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
                    System.out.println("[Magma] Failed to initialize a ConsoleConfig values.");
                    t.printStackTrace();
                }
            }
        }
        load();
    }

    @Override
    protected void addCommands() {}

    @Override
    protected void load() {
        try {
            config = YamlConfiguration.loadConfiguration(configFile);
            StringBuilder header = new StringBuilder(MagmaConfig.HEADER + "\n");
            for (Value toggle : values.values()) {
                if (!toggle.description.equals("")) {
                    header.append("Value: ").append(toggle.path).append(" Default: ").append(toggle.key).append("   # ").append(toggle.description).append("\n");
                }

                config.addDefault(toggle.path, toggle.key);
                values.get(toggle.path).setValues(config.getString(toggle.path));
            }
            version = getInt("config-version", CONFIG_VERSION);
            set("config-version", CONFIG_VERSION);


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

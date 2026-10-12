package com.icywinds.soundify;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Small dependency-free config so this client mod does not need Mod Menu or YACL. */
public final class SoundifyConfig {
    private static final Properties VALUES = new Properties();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("soundify.properties");

    private SoundifyConfig() {}

    public static synchronized void load() {
        VALUES.clear();
        VALUES.setProperty("enabled", "true");
        VALUES.setProperty("ui", "true");
        VALUES.setProperty("combat", "true");
        VALUES.setProperty("items", "true");
        VALUES.setProperty("footsteps", "true");
        VALUES.setProperty("blocks", "true");
        VALUES.setProperty("ambience", "true");
        VALUES.setProperty("low_health_alert", "true");
        VALUES.setProperty("level_up_alert", "true");

        try {
            Files.createDirectories(FILE.getParent());
            if (Files.exists(FILE)) {
                try (Reader reader = Files.newBufferedReader(FILE)) {
                    VALUES.load(reader);
                }
            } else {
                try (Writer writer = Files.newBufferedWriter(FILE)) {
                    VALUES.store(writer, "Soundify Reimagined settings. Use true or false.");
                }
            }
        } catch (IOException exception) {
            System.err.println("[Soundify] Could not load config; using defaults: " + exception.getMessage());
        }
    }

    public static boolean enabled(String category) {
        return Boolean.parseBoolean(VALUES.getProperty("enabled", "true"))
                && Boolean.parseBoolean(VALUES.getProperty(category, "true"));
    }
}

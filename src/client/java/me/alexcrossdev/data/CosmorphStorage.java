package me.alexcrossdev.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.alexcrossdev.CosmorphClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CosmorphStorage {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Type TYPE = new TypeToken<Map<String, CosmorphData>>(){}.getType();
    private static Map<String, CosmorphData> cache = new HashMap<>();

    private static final File FILE = new File(CosmorphClient.MOD_CONFIG_DIR, "cosmorph_data.json");

    public static void load() {
        try {
            if (!FILE.exists()) return;
            FileReader reader = new FileReader(FILE);
            cache = GSON.fromJson(reader, TYPE);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            if (!FILE.getParentFile().exists()) FILE.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(FILE);
            GSON.toJson(cache, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void set(UUID uuid, CosmorphData data) {
        cache.put(uuid.toString(), data);
        save();
    }

    public static void remove(UUID uuid) {
        cache.remove(uuid.toString());
        save();
    }

    public static CosmorphData get(UUID uuid) {
        return cache.get(uuid.toString());
    }

    public static Map<String, CosmorphData> getAll() {
        return cache;
    }
}
package net.flapjackpants.dynamicbrightnesscontrols.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import net.fabricmc.loader.api.FabricLoader;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "dynamicbrightnesscontrols.json");

    private static ModConfig INSTANCE = new ModConfig();

    public double overworldGamma = 0.0;
    public double nightGamma = 1.0;
    public double caveGamma = 1.0;
    public double netherGamma = 1.0;
    public double endGamma = 1.0;
    public double gammaTransitionTime = 1.0;

    public static ModConfig get() {
        if (INSTANCE == null){
            load();
        }
        return INSTANCE;
    }

    public static void load(){
        if (CONFIG_FILE.exists()){
            try (FileReader reader = new FileReader(CONFIG_FILE)){
                INSTANCE = GSON.fromJson(reader, ModConfig.class);
            } catch (IOException e){
                e.printStackTrace();
                INSTANCE = new ModConfig(); // Fallback if exception
            }
        } else {
            INSTANCE = new ModConfig();
            save(); // Create file with defaults
        }
    }

    public static void save(){
        try (FileWriter writer = new FileWriter(CONFIG_FILE)){
            GSON.toJson(INSTANCE, writer);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}


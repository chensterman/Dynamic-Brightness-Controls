package net.flapjackpants.dynamicbrightnesscontrols.config;

public class ModConfig {
    public double overworldGamma = 30.0;
    public double caveGamma = 100.0;
    public double netherGamma = 100.0;
    public double endGamma = 100.0;

    private static final ModConfig INSTANCE = new ModConfig();

    public static ModConfig get() {
        return INSTANCE;
    }
}


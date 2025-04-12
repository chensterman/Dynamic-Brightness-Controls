package net.flapjackpants.dynamicbrightnesscontrols.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class BrightnessConfigScreen {

    public static Screen create(Screen parent) {
        ModConfig config = ModConfig.get();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Dynamic Brightness Controls"));

        builder.setSavingRunnable(() -> {
            // Save config to file here if you implement persistence
            System.out.println("Saved brightness config!");
        });

        ConfigCategory gammaSettings = builder.getOrCreateCategory(Text.literal("Gamma Settings"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        gammaSettings.addEntry(entryBuilder.startDoubleField(
                        Text.literal("Overworld Gamma"), config.overworldGamma)
                .setDefaultValue(30.0)
                .setMin(0.0)
                .setMax(10000.0)
                .setSaveConsumer(newValue -> config.overworldGamma = newValue)
                .build()
        );

        gammaSettings.addEntry(entryBuilder.startDoubleField(
                        Text.literal("Cave Gamma"), config.caveGamma)
                .setDefaultValue(100.0)
                .setMin(0.0)
                .setMax(10000.0)
                .setSaveConsumer(newValue -> config.caveGamma = newValue)
                .build()
        );

        gammaSettings.addEntry(entryBuilder.startDoubleField(
                        Text.literal("Nether Gamma"), config.netherGamma)
                .setDefaultValue(100.0)
                .setMin(0.0)
                .setMax(10000.0)
                .setSaveConsumer(newValue -> config.netherGamma = newValue)
                .build()
        );

        gammaSettings.addEntry(entryBuilder.startDoubleField(
                        Text.literal("End Gamma"), config.endGamma)
                .setDefaultValue(100.0)
                .setMin(0.0)
                .setMax(10000.0)
                .setSaveConsumer(newValue -> config.endGamma = newValue)
                .build()
        );

        return builder.build();
    }
}


package net.flapjackpants.dynamicbrightnesscontrols;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.dimension.DimensionTypes;
import net.flapjackpants.dynamicbrightnesscontrols.mixin.GameOptionsMixin;

import java.util.Optional;

public class BrightnessHandler {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static double targetGamma = .3; //Default targetGamma

    public static void updateBrightness (){
        if (client.player == null || client.world == null) return;

        int lightLevel = client.world.getLightLevel(LightType.BLOCK, client.player.getBlockPos());
        boolean hasSkyLight = client.world.getLightLevel(LightType.SKY, client.player.getBlockPos()) > 7;

        System.out.println(", Target Brightness: " + targetGamma);

        // Adjust targetGamma
        if (client.world.getDimensionEntry().matchesKey(DimensionTypes.OVERWORLD)){
            targetGamma = hasSkyLight ? 0 : 1; // No Skylight = 100, with SkyLight = 30
        } else if (client.world.getDimensionEntry().matchesKey(DimensionTypes.THE_NETHER)){
            targetGamma = 1;
        } else if (client.world.getDimensionEntry().matchesKey(DimensionTypes.THE_END)){
            targetGamma = 1;
        }

        // Apply smooth transition
        GameOptions options = client.options;
        double currentGamma = options.getGamma().getValue();
        if (currentGamma < targetGamma) {
            options.getGamma().setValue(Math.min((currentGamma + .01), 1.0));
        } else if (currentGamma > targetGamma) {
            options.getGamma().setValue(Math.max((currentGamma - .01), 0.0));
        }
    }
}

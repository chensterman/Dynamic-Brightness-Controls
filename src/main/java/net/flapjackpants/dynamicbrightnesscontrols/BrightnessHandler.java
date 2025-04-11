package net.flapjackpants.dynamicbrightnesscontrols;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.world.LightType;
import net.minecraft.world.dimension.DimensionTypes;
import net.flapjackpants.dynamicbrightnesscontrols.mixin.MixinSimpleOption;

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
            targetGamma = hasSkyLight ? 0 : 20; // No Skylight = 100, with SkyLight = 30
        } else if (client.world.getDimensionEntry().matchesKey(DimensionTypes.THE_NETHER)){
            targetGamma = 1;
        } else if (client.world.getDimensionEntry().matchesKey(DimensionTypes.THE_END)){
            targetGamma = 1;
        }

        // Apply smooth transition
        GameOptions options = client.options;
        double currentGamma = options.getGamma().getValue();
        if (currentGamma < targetGamma) {
            currentGamma = Math.min(currentGamma + (targetGamma-currentGamma)/20.0, targetGamma);
        } else if (currentGamma > targetGamma) {
            currentGamma = Math.max(currentGamma + (targetGamma-currentGamma)/20.0, 0.0);
        }

        @SuppressWarnings("unchecked")
        MixinSimpleOption<Double> gammaOption = (MixinSimpleOption<Double>) (Object) client.options.getGamma();
        gammaOption.setValueNoCheck(currentGamma);
    }
}

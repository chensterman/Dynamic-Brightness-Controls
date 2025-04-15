package net.flapjackpants.dynamicbrightnesscontrols;

import com.terraformersmc.modmenu.util.mod.Mod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.world.LightType;
import net.minecraft.world.dimension.DimensionTypes;
import net.flapjackpants.dynamicbrightnesscontrols.mixin.MixinSimpleOption;
import net.flapjackpants.dynamicbrightnesscontrols.config.ModConfig;

public class BrightnessHandler {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static double targetGamma = 0.0; //Default targetGamma

    public static void updateBrightness (){
        if (client.player == null || client.world == null) return;

        boolean hasSkyLight = client.world.getLightLevel(LightType.SKY, client.player.getBlockPos()) > 7;
        double day = ModConfig.get().overworldGamma;
        double night = ModConfig.get().nightGamma;
        double radTime = client.world.getTimeOfDay()/12000.0*Math.PI; // Converts time into a radian value to use in day-night cycle

        // Adjust targetGamma
        if (client.world.getDimensionEntry().matchesKey(DimensionTypes.OVERWORLD)){
            // Day-night sinusoidal gamma curve
            targetGamma = Math.round(Math.max((Math.min((night-day)*(-.707107)*Math.sin(radTime)+.5, night)), day)*100)/100.0;
            if (!hasSkyLight) targetGamma = ModConfig.get().caveGamma; // check if in a place with no skylight access

        } else if (client.world.getDimensionEntry().matchesKey(DimensionTypes.THE_NETHER)){
            targetGamma = ModConfig.get().netherGamma;
        } else if (client.world.getDimensionEntry().matchesKey(DimensionTypes.THE_END)){
            targetGamma = ModConfig.get().endGamma;
        }

        // Apply smooth transition
        GameOptions options = client.options;
        double currentGamma = options.getGamma().getValue();
        if (currentGamma < targetGamma) {
            currentGamma = Math.round(Math.min(currentGamma + (targetGamma-currentGamma)/ModConfig.get().gammaTransitionTime/10.0, targetGamma)*10000)/10000.0;
        } else if (currentGamma > targetGamma) {
            currentGamma = Math.round(Math.max(currentGamma + (targetGamma-currentGamma)/ModConfig.get().gammaTransitionTime/10.0, targetGamma)*10000)/10000.0;
        }

        //System.out.println(" Time: " + client.world.getTimeOfDay()%24000+ " Target: "+ targetGamma + " Current: " + currentGamma);

        @SuppressWarnings("unchecked")
        MixinSimpleOption<Double> gammaOption = (MixinSimpleOption<Double>) (Object) client.options.getGamma();
        gammaOption.setValueNoCheck(currentGamma);
    }
}

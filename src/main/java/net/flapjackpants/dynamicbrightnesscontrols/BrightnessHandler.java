package net.flapjackpants.dynamicbrightnesscontrols;

import com.terraformersmc.modmenu.util.mod.Mod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.world.LightType;
import net.minecraft.world.dimension.DimensionTypes;
import net.flapjackpants.dynamicbrightnesscontrols.mixin.MixinSimpleOption;
import net.flapjackpants.dynamicbrightnesscontrols.config.ModConfig;
import net.minecraft.client.util.math.MatrixStack;

public class BrightnessHandler {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static double targetGamma = 0.0; //Default targetGamma
    private static float targetRainbowIntensity = 0.0f; //Default rainbow intensity

    /**
     * Updates both the brightness (gamma) and rainbow effect intensity based on game conditions
     */
    public static void updateBrightness(){
        if (client.player == null || client.world == null || !ModConfig.get().modEnabled) {
            // Reset rainbow effect when disabled
            RainbowRenderer.setIntensity(0.0f);
            return;
        }

        boolean hasSkyLight = client.world.getLightLevel(LightType.SKY, client.player.getBlockPos()) > 7;
        double day = ModConfig.get().overworldGamma;
        double night = ModConfig.get().nightGamma;
        double radTime = client.world.getTimeOfDay()/12000.0*Math.PI; // Converts time into a radian value to use in day-night cycle

        // Adjust targetGamma and rainbow intensity
        if (client.world.getDimensionEntry().matchesKey(DimensionTypes.OVERWORLD)){
            // Day-night sinusoidal gamma curve
            targetGamma = Math.max((Math.min((night-day)*(-.707107)*Math.sin(radTime)+(night+day)/2.0, night)), day);
            if (!hasSkyLight) targetGamma = ModConfig.get().caveGamma; // check if in a place with no skylight access
            
            // Set rainbow intensity based on gamma
            targetRainbowIntensity = (float)(targetGamma / ModConfig.get().maxGammaForRainbow);

        } else if (client.world.getDimensionEntry().matchesKey(DimensionTypes.THE_NETHER)){
            targetGamma = ModConfig.get().netherGamma;
            targetRainbowIntensity = (float)(targetGamma / ModConfig.get().maxGammaForRainbow);
        } else if (client.world.getDimensionEntry().matchesKey(DimensionTypes.THE_END)){
            targetGamma = ModConfig.get().endGamma;
            targetRainbowIntensity = (float)(targetGamma / ModConfig.get().maxGammaForRainbow);
        }

        // Apply smooth transition for gamma
        GameOptions options = client.options;
        double currentGamma = options.getGamma().getValue();
        if (currentGamma < targetGamma) {
            currentGamma = Math.round(Math.min(currentGamma + (targetGamma-currentGamma)/ModConfig.get().gammaTransitionTime/10.0, targetGamma)*10000)/10000.0;
        } else if (currentGamma > targetGamma) {
            currentGamma = Math.round(Math.max(currentGamma + (targetGamma-currentGamma)/ModConfig.get().gammaTransitionTime/10.0, targetGamma)*10000)/10000.0;
        }

        // Apply smooth transition for rainbow intensity
        float currentRainbowIntensity = targetRainbowIntensity;
        // Clamp rainbow intensity between 0 and 1
        currentRainbowIntensity = Math.max(0.0f, Math.min(1.0f, currentRainbowIntensity));
        
        // Update the rainbow renderer with new intensity
        RainbowRenderer.setIntensity(currentRainbowIntensity * ModConfig.get().rainbowIntensityMultiplier);

        //System.out.println(" Time: " + client.world.getTimeOfDay()%24000+ " Target: "+ targetGamma + " Rainbow: " + currentRainbowIntensity);

        @SuppressWarnings("unchecked")
        MixinSimpleOption<Double> gammaOption = (MixinSimpleOption<Double>) (Object) client.options.getGamma();
        gammaOption.setValueNoCheck(currentGamma);
    }
    
    /**
     * Renders the rainbow effect on the screen
     * @param matrices The matrix stack for rendering
     */
    public static void renderRainbowEffect(MatrixStack matrices) {
        RainbowRenderer.render(matrices);
    }
}

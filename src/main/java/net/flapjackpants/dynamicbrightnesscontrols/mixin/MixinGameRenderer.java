package net.flapjackpants.dynamicbrightnesscontrols.mixin;

import net.flapjackpants.dynamicbrightnesscontrols.BrightnessHandler;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to inject our rainbow rendering into the game's render pipeline.
 */
@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    
    /**
     * Injects our rainbow effect rendering after the game has rendered everything else
     * but before the HUD is rendered.
     */
    @Inject(method = "render", at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V",
            shift = At.Shift.BEFORE))
    private void renderRainbowEffect(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        MatrixStack matrixStack = new MatrixStack();
        BrightnessHandler.renderRainbowEffect(matrixStack);
    }
}

package net.flapjackpants.dynamicbrightnesscontrols.mixin;

import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleOption.class)
public interface MixinSimpleOption<T> {
    @Accessor("value")
    void setValueNoCheck(T value);
}

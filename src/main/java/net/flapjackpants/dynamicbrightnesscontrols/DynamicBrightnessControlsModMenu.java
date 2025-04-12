package net.flapjackpants.dynamicbrightnesscontrols;

import net.flapjackpants.dynamicbrightnesscontrols.config.BrightnessConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class DynamicBrightnessControlsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> BrightnessConfigScreen.create(parent);
    }
}

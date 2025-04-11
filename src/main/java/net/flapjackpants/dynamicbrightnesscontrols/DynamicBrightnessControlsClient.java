package net.flapjackpants.dynamicbrightnesscontrols;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class DynamicBrightnessControlsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> BrightnessHandler.updateBrightness());
    }
}

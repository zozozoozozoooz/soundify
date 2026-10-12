package com.icywinds.soundify;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;

public final class SoundifyClient implements ClientModInitializer {
    private static final float LOW_HEALTH_THRESHOLD = 6.0F; // three hearts
    private static final int HEARTBEAT_COOLDOWN_TICKS = 45;

    private int heartbeatCooldown;
    private int lastLevel = -1;
    private float lastHealth = -1.0F;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.level == null) {
                heartbeatCooldown = 0;
                lastLevel = -1;
                lastHealth = -1.0F;
                return;
            }

            var player = client.player;
            int level = player.experienceLevel;
            if (lastLevel >= 0 && level > lastLevel && SoundifyConfig.enabled("level_up_alert")) {
                client.getSoundManager().play(SimpleSoundInstance.forUI(SoundifyMod.LEVEL_UP, 0.9F));
            }
            lastLevel = level;

            float health = player.getHealth();
            if (lastHealth >= 0.0F
                    && health < lastHealth
                    && health > 0.0F
                    && health <= LOW_HEALTH_THRESHOLD
                    && heartbeatCooldown == 0
                    && SoundifyConfig.enabled("low_health_alert")) {
                client.getSoundManager().play(SimpleSoundInstance.forUI(SoundifyMod.HEARTBEAT, 0.82F));
                heartbeatCooldown = HEARTBEAT_COOLDOWN_TICKS;
            }
            lastHealth = health;

            if (heartbeatCooldown > 0) heartbeatCooldown--;
            if (health > LOW_HEALTH_THRESHOLD) heartbeatCooldown = 0;
        });
    }
}

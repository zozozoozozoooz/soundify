package com.icywinds.soundify;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public final class SoundifyMod implements ModInitializer {
    public static final String MOD_ID = "soundify";

    public static final SoundEvent COMBAT_HIT = register("combat_hit");
    public static final SoundEvent COMBAT_CRIT = register("combat_crit");
    public static final SoundEvent COMBAT_SWEEP = register("combat_sweep");
    public static final SoundEvent DAMAGE = register("damage");
    public static final SoundEvent DEATH_STING = register("death_sting");
    public static final SoundEvent HEARTBEAT = register("heartbeat");
    public static final SoundEvent LEVEL_UP = register("level_up");
    public static final SoundEvent UI_CLICK = register("ui_click");
    public static final SoundEvent UI_HOVER = register("ui_hover");
    public static final SoundEvent ITEM_PICKUP = register("item_pickup");
    public static final SoundEvent XP_PICKUP = register("xp_pickup");
    public static final SoundEvent ARMOR_EQUIP = register("armor_equip");
    public static final SoundEvent ITEM_USE = register("item_use");
    public static final SoundEvent EAT = register("eat");
    public static final SoundEvent DRINK = register("drink");
    public static final SoundEvent FOOTSTEP_GRASS = register("footstep_grass");
    public static final SoundEvent FOOTSTEP_STONE = register("footstep_stone");
    public static final SoundEvent FOOTSTEP_WOOD = register("footstep_wood");
    public static final SoundEvent FOOTSTEP_SAND = register("footstep_sand");
    public static final SoundEvent FOOTSTEP_GRAVEL = register("footstep_gravel");
    public static final SoundEvent FOOTSTEP_SNOW = register("footstep_snow");
    public static final SoundEvent FOOTSTEP_METAL = register("footstep_metal");
    public static final SoundEvent CHEST_OPEN = register("chest_open");
    public static final SoundEvent CHEST_CLOSE = register("chest_close");
    public static final SoundEvent DOOR_OPEN = register("door_open");
    public static final SoundEvent DOOR_CLOSE = register("door_close");
    public static final SoundEvent BLOCK_BREAK = register("block_break");
    public static final SoundEvent METAL_CLANK = register("metal_clank");
    public static final SoundEvent CAVE_AMBIENCE = register("cave_ambience");
    public static final SoundEvent WATER_SPLASH = register("water_splash");

    private static SoundEvent register(String path) {
        Identifier id = Identifier.fromNamespaceAndPath(MOD_ID, path);
        return Registry.register(
                BuiltInRegistries.SOUND_EVENT,
                id,
                SoundEvent.createVariableRangeEvent(id)
        );
    }

    @Override
    public void onInitialize() {
        SoundifyConfig.load();
    }
}

package com.icywinds.soundify.mixin;

import com.icywinds.soundify.SoundifyConfig;
import com.icywinds.soundify.SoundifyMod;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Map;

/** Replaces selected vanilla sound events while preserving source, volume, pitch and position. */
@Mixin(SoundManager.class)
public abstract class SoundManagerMixin {
    private record Replacement(SoundEvent sound, String category) {}

    private static final Map<String, Replacement> REPLACEMENTS = Map.ofEntries(
            Map.entry("ui.button.click", new Replacement(SoundifyMod.UI_CLICK, "ui")),
            Map.entry("entity.player.attack.strong", new Replacement(SoundifyMod.COMBAT_HIT, "combat")),
            Map.entry("entity.player.attack.weak", new Replacement(SoundifyMod.COMBAT_HIT, "combat")),
            Map.entry("entity.player.attack.knockback", new Replacement(SoundifyMod.COMBAT_HIT, "combat")),
            Map.entry("entity.player.attack.crit", new Replacement(SoundifyMod.COMBAT_CRIT, "combat")),
            Map.entry("entity.player.attack.sweep", new Replacement(SoundifyMod.COMBAT_SWEEP, "combat")),
            Map.entry("entity.player.hurt", new Replacement(SoundifyMod.DAMAGE, "combat")),
            Map.entry("entity.player.death", new Replacement(SoundifyMod.DEATH_STING, "combat")),
            Map.entry("entity.item.pickup", new Replacement(SoundifyMod.ITEM_PICKUP, "items")),
            Map.entry("entity.experience_orb.pickup", new Replacement(SoundifyMod.XP_PICKUP, "items")),
            Map.entry("item.armor.equip_generic", new Replacement(SoundifyMod.ARMOR_EQUIP, "items")),
            Map.entry("entity.generic.eat", new Replacement(SoundifyMod.EAT, "items")),
            Map.entry("entity.generic.drink", new Replacement(SoundifyMod.DRINK, "items")),
            Map.entry("item.spyglass.use", new Replacement(SoundifyMod.ITEM_USE, "items")),
            Map.entry("block.grass.step", new Replacement(SoundifyMod.FOOTSTEP_GRASS, "footsteps")),
            Map.entry("block.stone.step", new Replacement(SoundifyMod.FOOTSTEP_STONE, "footsteps")),
            Map.entry("block.wood.step", new Replacement(SoundifyMod.FOOTSTEP_WOOD, "footsteps")),
            Map.entry("block.sand.step", new Replacement(SoundifyMod.FOOTSTEP_SAND, "footsteps")),
            Map.entry("block.gravel.step", new Replacement(SoundifyMod.FOOTSTEP_GRAVEL, "footsteps")),
            Map.entry("block.snow.step", new Replacement(SoundifyMod.FOOTSTEP_SNOW, "footsteps")),
            Map.entry("block.metal.step", new Replacement(SoundifyMod.FOOTSTEP_METAL, "footsteps")),
            Map.entry("block.chest.open", new Replacement(SoundifyMod.CHEST_OPEN, "blocks")),
            Map.entry("block.chest.close", new Replacement(SoundifyMod.CHEST_CLOSE, "blocks")),
            Map.entry("block.wooden_door.open", new Replacement(SoundifyMod.DOOR_OPEN, "blocks")),
            Map.entry("block.wooden_door.close", new Replacement(SoundifyMod.DOOR_CLOSE, "blocks")),
            Map.entry("block.stone.break", new Replacement(SoundifyMod.BLOCK_BREAK, "blocks")),
            Map.entry("block.anvil.use", new Replacement(SoundifyMod.METAL_CLANK, "blocks")),
            Map.entry("ambient.cave", new Replacement(SoundifyMod.CAVE_AMBIENCE, "ambience")),
            Map.entry("entity.player.splash", new Replacement(SoundifyMod.WATER_SPLASH, "ambience")),
            Map.entry("entity.player.splash.high_speed", new Replacement(SoundifyMod.WATER_SPLASH, "ambience"))
    );

    @ModifyVariable(
            method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)Lnet/minecraft/client/sounds/SoundEngine$PlayResult;",
            at = @At("HEAD"),
            argsOnly = true
    )
    private SoundInstance soundify$replaceVanillaSound(SoundInstance original) {
        if (original == null) {
            return original;
        }

        Identifier id = original.getIdentifier();
        if (id == null || !"minecraft".equals(id.getNamespace())) {
            return original;
        }

        Replacement replacement = REPLACEMENTS.get(id.getPath());
        if (replacement == null || !SoundifyConfig.enabled(replacement.category())) {
            return original;
        }

        return new SimpleSoundInstance(
                replacement.sound(),
                original.getSource(),
                original.getVolume(),
                original.getPitch(),
                RandomSource.create(),
                original.getX(),
                original.getY(),
                original.getZ()
        );
    }
}

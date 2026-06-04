package com.example.magic_ritual_mod.worldgen;

import com.example.magic_ritual_mod.MagicRitualMod;
import com.example.magic_ritual_mod.worldgen.custom.SpiritOreVeinFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, MagicRitualMod.MODID);

    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SPIRIT_ORE_VEIN =
            FEATURES.register("spirit_ore_vein", SpiritOreVeinFeature::new);

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}

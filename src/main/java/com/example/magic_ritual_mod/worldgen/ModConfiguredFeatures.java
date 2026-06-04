package com.example.magic_ritual_mod.worldgen;

import com.example.magic_ritual_mod.MagicRitualMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPIRIT_ORE_VEIN_KEY =
            ResourceKey.create(Registries.CONFIGURED_FEATURE,
                    ResourceLocation.fromNamespaceAndPath(MagicRitualMod.MODID, "spirit_ore_vein"));

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        context.register(SPIRIT_ORE_VEIN_KEY,
                new ConfiguredFeature<>(ModFeatures.SPIRIT_ORE_VEIN.get(), NoneFeatureConfiguration.INSTANCE));
    }
}

package com.example.magic_ritual_mod.effect;

import com.example.magic_ritual_mod.MagicRitualMod;
import com.example.magic_ritual_mod.effect.custom.BleedingEffect;
import com.example.magic_ritual_mod.effect.custom.FogEffect;
import com.example.magic_ritual_mod.effect.custom.InvertedControlEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, MagicRitualMod.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> BLEEDING =
            EFFECTS.register("bleeding", () -> new BleedingEffect(
                    MobEffectCategory.HARMFUL,
                    0x8B0000
            ));

    public static final DeferredHolder<MobEffect, MobEffect> FOG =
            EFFECTS.register("fog", () -> new FogEffect(
                    MobEffectCategory.HARMFUL,
                    0xFFFFFF
            ));

    public static final DeferredHolder<MobEffect, MobEffect> INVERTED_CONTROL =
            EFFECTS.register("inverted_control", () -> new InvertedControlEffect(
                    MobEffectCategory.HARMFUL,
                    0x800080
            ));

    public static void register(IEventBus eventBus) {
        EFFECTS.register(eventBus);
    }
}

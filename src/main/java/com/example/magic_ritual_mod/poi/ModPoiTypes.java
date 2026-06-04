package com.example.magic_ritual_mod.poi;

import com.example.magic_ritual_mod.MagicRitualMod;
import com.example.magic_ritual_mod.block.ModBlocks;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModPoiTypes {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, MagicRitualMod.MODID);

    public static final DeferredHolder<PoiType, PoiType> SPIRIT_VEIN_CENTER_POI =
            POI_TYPES.register("spirit_vein_center",
                    () -> new PoiType(
                            ImmutableSet.copyOf(ModBlocks.SPIRIT_VEIN_CENTER.get().getStateDefinition().getPossibleStates()),
                            0, 1
                    ));

    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
    }
}

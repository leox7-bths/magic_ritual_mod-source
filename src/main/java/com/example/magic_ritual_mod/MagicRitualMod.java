package com.example.magic_ritual_mod;

import com.example.magic_ritual_mod.block.ModBlocks;
import com.example.magic_ritual_mod.block.custom.ModBlockEntities;
import com.example.magic_ritual_mod.effect.ModEffects;
import com.example.magic_ritual_mod.item.ModItems;
import com.example.magic_ritual_mod.poi.ModPoiTypes;
import com.example.magic_ritual_mod.ui.ModMenuTypes;
import com.example.magic_ritual_mod.ui.ModTabs;
import com.example.magic_ritual_mod.worldgen.ModFeatures;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(MagicRitualMod.MODID)
public class MagicRitualMod {

    public static final String MODID = "magic_ritual_mod";

    public MagicRitualMod(IEventBus modEventBus, ModContainer modContainer) {

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);

        ModTabs.CREATIVE_TABS.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModEffects.register(modEventBus);
        ModFeatures.register(modEventBus);
        ModPoiTypes.register(modEventBus);
    }
}
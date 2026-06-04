package com.example.magic_ritual_mod.ui;

import com.example.magic_ritual_mod.MagicRitualMod;
import com.example.magic_ritual_mod.block.ModBlocks;
import com.example.magic_ritual_mod.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MagicRitualMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAGIC_RITUAL_TAB =
            CREATIVE_TABS.register("magic_ritual_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MagicRitualMod.MODID))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.RITUAL_DAGGER.get().getDefaultInstance())
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.RITUAL_DAGGER.get());
                        output.accept(ModItems.SPIRIT_STONE.get());
                        output.accept(ModBlocks.MAGIC_CENTER_BLOCK.get());
                        output.accept(ModBlocks.MAGIC_CIRCLE_BLOCK.get());
                        output.accept(ModBlocks.SPIRIT_ORE.get());
                        output.accept(ModItems.SPIRIT_MAP.get());
                    }).build());
}

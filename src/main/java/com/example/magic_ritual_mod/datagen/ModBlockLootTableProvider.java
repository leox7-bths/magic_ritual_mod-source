package com.example.magic_ritual_mod.datagen;

import com.example.magic_ritual_mod.block.ModBlocks;
import com.example.magic_ritual_mod.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {

    protected ModBlockLootTableProvider(HolderLookup.Provider lookupProvider) {
        super(Set.of(), FeatureFlags.DEFAULT_FLAGS, lookupProvider);
    }

    @Override
    protected void generate() {
        add(ModBlocks.SPIRIT_ORE.get(),
                createOreDrop(ModBlocks.SPIRIT_ORE.get(), ModItems.SPIRIT_STONE.get()));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(e -> (Block) e.get())
                .filter(b -> b != ModBlocks.MAGIC_CENTER_BLOCK.get()
                        && b != ModBlocks.MAGIC_CIRCLE_BLOCK.get()
                        && b != ModBlocks.SPIRIT_VEIN_CENTER.get()
                        && b != ModBlocks.COMBO_BLOCK.get())
                .toList();
    }
}

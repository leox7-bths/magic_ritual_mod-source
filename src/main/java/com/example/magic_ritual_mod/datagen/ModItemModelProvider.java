package com.example.magic_ritual_mod.datagen;

import com.example.magic_ritual_mod.MagicRitualMod;
import com.example.magic_ritual_mod.block.ModBlocks;
import com.example.magic_ritual_mod.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MagicRitualMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.RITUAL_DAGGER.get());
        basicItem(ModItems.SPIRIT_STONE.get());
        basicItem(ModBlocks.MAGIC_CENTER_BLOCK.get().asItem());
        basicItem(ModItems.SPIRIT_MAP.get());
    }
}

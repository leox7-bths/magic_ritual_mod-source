package com.example.magic_ritual_mod.datagen;

import com.example.magic_ritual_mod.MagicRitualMod;
import com.example.magic_ritual_mod.block.ModBlocks;
import com.example.magic_ritual_mod.block.custom.CircleType;
import com.example.magic_ritual_mod.block.custom.MagicCircleBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MagicRitualMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlockWithItem(ModBlocks.SPIRIT_ORE.get(),
                models().cubeAll("spirit_ore", modLoc("block/spirit_ore")));


        ModelFile attackModel = makeCircleModel(CircleType.ATTACK, modLoc("block/magic_circle_attack"));
        ModelFile defenseModel = makeCircleModel(CircleType.DEFENSE, modLoc("block/magic_circle_defense"));
        ModelFile regenModel = makeCircleModel(CircleType.REGENERATION, modLoc("block/magic_circle_regeneration"));

        getVariantBuilder(ModBlocks.MAGIC_CIRCLE_BLOCK.get())
                .partialState().with(MagicCircleBlock.CIRCLE_TYPE, CircleType.ATTACK)
                .modelForState().modelFile(attackModel).addModel()
                .partialState().with(MagicCircleBlock.CIRCLE_TYPE, CircleType.DEFENSE)
                .modelForState().modelFile(defenseModel).addModel()
                .partialState().with(MagicCircleBlock.CIRCLE_TYPE, CircleType.REGENERATION)
                .modelForState().modelFile(regenModel).addModel();

        simpleBlockItem(ModBlocks.MAGIC_CIRCLE_BLOCK.get(), attackModel);
    }

    private ModelFile makeCircleModel(CircleType type, ResourceLocation texture) {
        return models().singleTexture(
                "block/magic_circle_block_" + type.getSerializedName(),
                mcLoc("block/carpet"),
                "wool",
                texture
        );
    }}

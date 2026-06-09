package com.example.magic_ritual_mod.block;

import com.example.magic_ritual_mod.MagicRitualMod;
import com.example.magic_ritual_mod.block.custom.*;
import com.example.magic_ritual_mod.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(MagicRitualMod.MODID);

    // ================= BLOCKS =================

    public static final DeferredBlock<Block> MAGIC_CENTER_BLOCK =
            registerBlock("magic_center_block",
                    () -> new MagicCenterBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.NONE)
                            .strength(0.2f)
                            .sound(SoundType.STONE)
                            .noOcclusion()));

    public static final DeferredBlock<Block> MAGIC_CIRCLE_BLOCK =
            registerBlock("magic_circle_block",
                    () -> new MagicCircleBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_RED)
                            .strength(0.1f)
                            .sound(SoundType.WOOL)
                            .noOcclusion()));

    public static final DeferredBlock<Block> SPIRIT_ORE =
            registerBlock("spirit_ore",
                    () -> new Block(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_PURPLE)
                            .strength(3.0f, 3.0f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> SPIRIT_VEIN_CENTER =
            registerBlock("spirit_vein_center",
                    () -> new SpiritVeinCenterBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.NONE)
                            .strength(0.5f)
                            .sound(SoundType.STONE)
                            .noOcclusion()));

    public static final DeferredBlock<Block> COMBO_BLOCK = BLOCKS.<Block>register("combo_block",
            () -> new ComboBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NONE)
                    .strength(0.2f)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .noLootTable()));

    public static final DeferredBlock<Block> FOG_ESCAPE_BLOCK = BLOCKS.<Block>register("fog_escape_block",
            () -> new FogEscapeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NONE)
                    .strength(0.2f)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .noLootTable()));

    // ================= BLOCK ITEMS =================

    public static final DeferredItem<BlockItem> MAGIC_CENTER_BLOCK_ITEM =
            ModItems.ITEMS.register("magic_center_block",
                    () -> new BlockItem(MAGIC_CENTER_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> MAGIC_CIRCLE_BLOCK_ITEM =
            ModItems.ITEMS.register("magic_circle_block",
                    () -> new BlockItem(MAGIC_CIRCLE_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> SPIRIT_ORE_ITEM =
            ModItems.ITEMS.register("spirit_ore",
                    () -> new BlockItem(SPIRIT_ORE.get(), new Item.Properties()));

    public static final DeferredItem<BlockItem> SPIRIT_VEIN_CENTER_ITEM =
            ModItems.ITEMS.register("spirit_vein_center",
                    () -> new BlockItem(SPIRIT_VEIN_CENTER.get(), new Item.Properties()));

    // ================= REGISTER HELPER =================

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }
}

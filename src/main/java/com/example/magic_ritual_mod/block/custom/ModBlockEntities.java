package com.example.magic_ritual_mod.block.custom;

import com.example.magic_ritual_mod.MagicRitualMod;
import com.example.magic_ritual_mod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MagicRitualMod.MODID);

    public static final Supplier<BlockEntityType<MagicCircleBlockEntity>> MAGIC_CIRCLE_BE =
            BLOCK_ENTITIES.register("magic_circle_be",
                    () -> BlockEntityType.Builder.of(
                            MagicCircleBlockEntity::new,
                            ModBlocks.MAGIC_CIRCLE_BLOCK.get()
                    ).build(null)
            );

    public static final Supplier<BlockEntityType<MagicCenterBlockEntity>> MAGIC_CENTER_BE =
            BLOCK_ENTITIES.register("magic_center_be",
                    () -> BlockEntityType.Builder.of(
                            MagicCenterBlockEntity::new,
                            ModBlocks.MAGIC_CENTER_BLOCK.get()
                    ).build(null)
            );

    public static final Supplier<BlockEntityType<SpiritVeinCenterBlockEntity>> SPIRIT_VEIN_CENTER_BE =
            BLOCK_ENTITIES.register("spirit_vein_center_be",
                    () -> BlockEntityType.Builder.of(
                            SpiritVeinCenterBlockEntity::new,
                            ModBlocks.SPIRIT_VEIN_CENTER.get()
                    ).build(null)
            );

    public static final Supplier<BlockEntityType<ComboBlockEntity>> COMBO_BE =
            BLOCK_ENTITIES.register("combo_be",
                    () -> BlockEntityType.Builder.of(
                            ComboBlockEntity::new,
                            ModBlocks.COMBO_BLOCK.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

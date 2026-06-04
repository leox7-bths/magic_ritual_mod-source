package com.example.magic_ritual_mod.worldgen.custom;

import com.example.magic_ritual_mod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SpiritOreVeinFeature extends Feature<NoneFeatureConfiguration> {

    public SpiritOreVeinFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();

        int y = -100 + random.nextInt(101);
        BlockPos origin = new BlockPos(
                context.origin().getX(),
                y,
                context.origin().getZ()
        );

        int oreCount = 200 + random.nextInt(51);
        int radius = 8;
        int placed = 0;
        int attempts = 0;
        int maxAttempts = oreCount * 4;

        while (placed < oreCount && attempts < maxAttempts) {
            attempts++;

            BlockPos placePos = origin.offset(
                    random.nextInt(radius * 2 + 1) - radius,
                    random.nextInt(radius * 2 + 1) - radius,
                    random.nextInt(radius * 2 + 1) - radius
            );

            if (placePos.getY() > 0) continue;

            if (Math.sqrt(placePos.distSqr(origin)) > radius - 0.5) continue;

            BlockState target = level.getBlockState(placePos);

            if (!target.is(BlockTags.BASE_STONE_OVERWORLD)) continue;

            if (random.nextInt(50) == 0) {
                level.setBlock(
                        placePos,
                        ModBlocks.SPIRIT_VEIN_CENTER.get().defaultBlockState(),
                        3
                );
            } else {
                level.setBlock(
                        placePos,
                        ModBlocks.SPIRIT_ORE.get().defaultBlockState(),
                        3
                );
            }

            placed++;
        }

        return placed > 0;
    }
}

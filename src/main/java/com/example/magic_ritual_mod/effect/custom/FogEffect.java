package com.example.magic_ritual_mod.effect.custom;

import com.example.magic_ritual_mod.MagicRitualMod;
import com.example.magic_ritual_mod.block.ModBlocks;
import com.example.magic_ritual_mod.block.custom.FogEscapeBlockEntity;
import com.example.magic_ritual_mod.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class FogEffect extends MobEffect {
    private static final ResourceKey<Level> EMPTY_DIMENSION = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath(MagicRitualMod.MODID, "empty")
    );
    private static final int RANDOM_COORD_RANGE = 100000;
    private static final int ESCAPE_BLOCK_RADIUS = 10;

    public FogEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayer player) {
            if (player.level().dimension() == EMPTY_DIMENSION) {
                player.addEffect(new MobEffectInstance(ModEffects.FOG, 80, amplifier, true, false));
            } else {
                teleportToEmptyDimension(player);
            }
        }

        return true;
    }

    private static void teleportToEmptyDimension(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }

        ServerLevel emptyLevel = server.getLevel(EMPTY_DIMENSION);
        if (emptyLevel == null) {
            return;
        }

        RandomSource random = player.getRandom();
        int x = random.nextInt(RANDOM_COORD_RANGE * 2 + 1) - RANDOM_COORD_RANGE;
        int z = random.nextInt(RANDOM_COORD_RANGE * 2 + 1) - RANDOM_COORD_RANGE;
        int y = safeSurfaceY(emptyLevel, x, z);

        BlockPos escapePos = findEscapeBlockPos(emptyLevel, random, x, y, z);

        emptyLevel.setBlock(escapePos, ModBlocks.FOG_ESCAPE_BLOCK.get().defaultBlockState(), 3);
        if (emptyLevel.getBlockEntity(escapePos) instanceof FogEscapeBlockEntity escapeBlockEntity) {
            escapeBlockEntity.setReturnLocation(player);
        }

        player.teleportTo(emptyLevel, x + 0.5, y, z + 0.5, player.getYRot(), player.getXRot());
    }

    private static BlockPos findEscapeBlockPos(ServerLevel level, RandomSource random, int x, int y, int z) {
        for (int attempts = 0; attempts < 32; attempts++) {
            int dx = random.nextInt(ESCAPE_BLOCK_RADIUS * 2 + 1) - ESCAPE_BLOCK_RADIUS;
            int dz = random.nextInt(ESCAPE_BLOCK_RADIUS * 2 + 1) - ESCAPE_BLOCK_RADIUS;
            if (dx * dx + dz * dz > ESCAPE_BLOCK_RADIUS * ESCAPE_BLOCK_RADIUS) {
                continue;
            }

            int escapeX = x + dx;
            int escapeZ = z + dz;
            int escapeY = safeSurfaceY(level, escapeX, escapeZ);
            BlockPos pos = new BlockPos(escapeX, escapeY, escapeZ);
            if (level.getBlockState(pos).isAir()) {
                return pos;
            }
        }

        return new BlockPos(x, y, z).offset(ESCAPE_BLOCK_RADIUS, 0, 0);
    }

    private static int safeSurfaceY(ServerLevel level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        while (!level.getBlockState(new BlockPos(x, y, z)).isAir()
                || !level.getBlockState(new BlockPos(x, y + 1, z)).isAir()) {
            y++;
        }
        return y;
    }
}

package com.example.magic_ritual_mod.block.custom;

import com.example.magic_ritual_mod.block.ModBlocks;
import com.example.magic_ritual_mod.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.joml.Vector3f;

public class ComboBlockEntity extends BlockEntity {
    private static final int EFFECT_DURATION = 100;
    private static final int EFFECT_INTERVAL = 20;


    public record PatternEntry(int dx, int dz, CircleType type) {}

    public static final PatternEntry[] FOG_PATTERN = {
        new PatternEntry(-1, -1, CircleType.ATTACK),
        new PatternEntry(-1, 1, CircleType.DEFENSE),
        new PatternEntry(1, 1, CircleType.ATTACK),
    };

    public static final PatternEntry[] INVERTED_PATTERN = {
        new PatternEntry(0, -1, CircleType.DEFENSE),
        new PatternEntry(-1, 1, CircleType.DEFENSE),
        new PatternEntry(1, 1, CircleType.DEFENSE),
    };

    public static PatternEntry[] getPattern(ComboType comboType) {
        return comboType == ComboType.FOG ? FOG_PATTERN : INVERTED_PATTERN;
    }

    public ComboBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMBO_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ComboBlockEntity be) {

        if (level.isClientSide) {
            be.spawnParticles(level, pos, state);
            return;
        }

        if (level.getGameTime() % EFFECT_INTERVAL != 0) return;

        ComboType comboType = state.getValue(ComboBlock.COMBO_TYPE);

        if (!isPatternValid(level, pos, comboType)) {
            level.destroyBlock(pos, false);
            return;
        }

        applyEffect(level, pos, comboType);
    }

    public static boolean isPatternValid(Level level, BlockPos pos, ComboType type) {
        PatternEntry[] entries = getPattern(type);
        for (PatternEntry entry : entries) {
            boolean found = false;
            for (int y = -1; y <= 1; y++) {
                BlockPos checkPos = pos.offset(entry.dx(), y, entry.dz());
                BlockState blockState = level.getBlockState(checkPos);
                if (blockState.is(ModBlocks.MAGIC_CIRCLE_BLOCK.get()) &&
                    blockState.getValue(MagicCircleBlock.CIRCLE_TYPE) == entry.type()) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    public static boolean isCirclePartOfActiveCombo(Level level, BlockPos circlePos) {
        for (ComboType comboType : ComboType.values()) {
            PatternEntry[] entries = getPattern(comboType);
            for (PatternEntry entry : entries) {
                BlockPos expectedSPos = circlePos.offset(-entry.dx(), 0, -entry.dz());
                for (int y = -1; y <= 1; y++) {
                    BlockPos checkPos = expectedSPos.offset(0, y, 0);
                    BlockState state = level.getBlockState(checkPos);
                    if (state.is(ModBlocks.COMBO_BLOCK.get()) &&
                        state.getValue(ComboBlock.COMBO_TYPE) == comboType) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void applyEffect(Level level, BlockPos pos, ComboType type) {
        DeferredHolder<MobEffect, MobEffect> effect = type == ComboType.FOG
            ? ModEffects.FOG : ModEffects.INVERTED_CONTROL;
        AABB area = new AABB(pos);
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
            entity.addEffect(new MobEffectInstance(effect, EFFECT_DURATION, 0, false, true, true));
        }
    }

    private void spawnParticles(Level level, BlockPos pos, BlockState state) {

        ComboType type = state.getValue(ComboBlock.COMBO_TYPE);

        double[][] points = switch (type) {
            case FOG -> MagicCenterBlockEntity.FOG_POINTS;
            case INVERTED -> MagicCenterBlockEntity.INVERTED_POINTS;
        };

        Vector3f color = switch (type) {
            case FOG -> new Vector3f(1.0F, 1.0F, 1.0F);
            case INVERTED -> new Vector3f(0.5F, 0.0F, 0.5F);
        };

        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.1;
        double cz = pos.getZ() + 0.5;

        long time = level.getGameTime();
        double angle = (time * 0.8) % 360;

        double rad = Math.toRadians(angle);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);

        double scale = 1.0;

        for (double[] p : points) {

            double px = p[0] * scale;
            double pz = p[1] * scale;

            double rx = px * cos - pz * sin;
            double rz = px * sin + pz * cos;

            level.addParticle(
                    new DustParticleOptions(color, 0.25F),
                    cx + rx, cy, cz + rz,
                    0, 0, 0
            );
        }
    }
}

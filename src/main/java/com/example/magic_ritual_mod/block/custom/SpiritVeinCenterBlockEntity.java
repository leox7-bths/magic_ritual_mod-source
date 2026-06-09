package com.example.magic_ritual_mod.block.custom;

import com.example.magic_ritual_mod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class SpiritVeinCenterBlockEntity extends BlockEntity {
    private static final String TAG_TICK_COUNTER = "tick_counter";
    private static final String TAG_STORED_SPIRIT_STONES = "stored_spirit_stones";
    private int tickCounter = 0;
    private int storedSpiritStones = 0;

    public SpiritVeinCenterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPIRIT_VEIN_CENTER_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpiritVeinCenterBlockEntity be) {

        if (!level.isClientSide) {

            be.tickCounter++;

            if (be.tickCounter >= 1200) {
                be.tickCounter = 0;

                if (be.hasBlockAbove()) {
                    be.storedSpiritStones++;
                    be.setChanged();
                } else {
                    be.dropSpiritStones(1);
                }
            }

            if (be.tickCounter % 2 == 0) {
                ((net.minecraft.server.level.ServerLevel) level).sendParticles(
                        new DustParticleOptions(new Vector3f(0.0F, 1.0F, 1.0F), 5.0F),
                        pos.getX() + 0.5,
                        pos.getY() + 0.5,
                        pos.getZ() + 0.5,
                        10,
                        0,
                        0,
                        0,
                        0
                );
            }
        }
    }

    public boolean releaseStoredSpiritStones() {
        if (storedSpiritStones <= 0) {
            return false;
        }

        dropSpiritStones(storedSpiritStones);
        storedSpiritStones = 0;
        setChanged();
        return true;
    }

    private boolean hasBlockAbove() {
        return level != null && !level.getBlockState(worldPosition.above()).isAir();
    }

    private void dropSpiritStones(int count) {
        if (level == null || count <= 0) {
            return;
        }

        Containers.dropItemStack(level,
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 1.0,
                worldPosition.getZ() + 0.5,
                new ItemStack(ModItems.SPIRIT_STONE.get(), count));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_TICK_COUNTER, this.tickCounter);
        tag.putInt(TAG_STORED_SPIRIT_STONES, this.storedSpiritStones);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.tickCounter = tag.getInt(TAG_TICK_COUNTER);
        this.storedSpiritStones = tag.getInt(TAG_STORED_SPIRIT_STONES);
    }
}

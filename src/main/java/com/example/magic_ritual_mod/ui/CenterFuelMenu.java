package com.example.magic_ritual_mod.ui;

import com.example.magic_ritual_mod.block.ModBlocks;
import com.example.magic_ritual_mod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.HashSet;
import java.util.Set;

public class CenterFuelMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final IItemHandler fuelHandler;

    public final BlockPos centerPos;
    public boolean allowHostileMobs;
    public boolean allowPassiveMobs;
    public final Set<String> blacklistedPlayers = new HashSet<>();

    private static final int FUEL_SLOT = 0;
    private static final int CONTAINER_END = 1;
    private static final int PLAYER_INV_START = 1;
    private static final int PLAYER_INV_END = 28;
    private static final int HOTBAR_END = 37;

    public boolean particlesEnabled;

    public CenterFuelMenu(int id, Inventory playerInv, RegistryFriendlyByteBuf extraData) {
        this(id, playerInv, new ItemStackHandler(1),
                ContainerLevelAccess.NULL,
                extraData.readBlockPos(),
                extraData.readBoolean(),
                extraData.readBoolean(),
                extraData.readCollection(HashSet::new, buf -> buf.readUtf()));
        this.particlesEnabled = extraData.readBoolean();
    }

    public CenterFuelMenu(int id, Inventory playerInv,
                          IItemHandler fuelHandler,
                          ContainerLevelAccess access,
                          BlockPos centerPos,
                          boolean allowHostileMobs,
                          boolean allowPassiveMobs,
                          Set<String> blacklistedPlayers) {
        super(ModMenuTypes.CENTER_FUEL.get(), id);
        this.access = access;
        this.fuelHandler = fuelHandler;
        this.centerPos = centerPos;
        this.allowHostileMobs = allowHostileMobs;
        this.allowPassiveMobs = allowPassiveMobs;
        this.blacklistedPlayers.addAll(blacklistedPlayers);

        int imageWidth = 176;
        int fuelSlotX = (imageWidth - 18) / 2;
        int playerInvX = (imageWidth - 162) / 2;
        int playerInvY = 155;

        addSlot(new SlotItemHandler(fuelHandler, 0, fuelSlotX, 5));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, 9 + row * 9 + col,
                        playerInvX + col * 18, playerInvY + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col,
                    playerInvX + col * 18, playerInvY + 58));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack quickMovedStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack rawStack = slot.getItem();
        quickMovedStack = rawStack.copy();

        if (slotIndex < CONTAINER_END) {
            if (!this.moveItemStackTo(rawStack, PLAYER_INV_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (rawStack.is(ModItems.SPIRIT_STONE.get())) {
                if (!this.moveItemStackTo(rawStack, 0, CONTAINER_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (rawStack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (rawStack.getCount() == quickMovedStack.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, rawStack);
        return quickMovedStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.MAGIC_CENTER_BLOCK.get());
    }
}

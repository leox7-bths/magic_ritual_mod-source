package com.example.magic_ritual_mod.ui;

import com.example.magic_ritual_mod.network.payload.ServerboundCenterSettingsPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class CenterFuelScreen extends AbstractContainerScreen<CenterFuelMenu> {
    private static final String NS = "autoforge_bricks";
    private static ResourceLocation tex(String name) {
        return ResourceLocation.fromNamespaceAndPath(NS, "textures/gui/" + name + ".png");
    }
    private static final ResourceLocation TEX_SLOT  = tex("slot_default");
    private static final ResourceLocation TEX_PSLOT = tex("player_slots_9x4");
    private static final ResourceLocation TEX_FILL  = tex("fill_white");
    private static final ResourceLocation TEX_SPLIT = tex("split_bar");
    private static final ResourceLocation TEX_TL = tex("border_corner_tl");
    private static final ResourceLocation TEX_TR = tex("border_corner_tr");
    private static final ResourceLocation TEX_BL = tex("border_corner_bl");
    private static final ResourceLocation TEX_BR = tex("border_corner_br");
    private static final ResourceLocation TEX_ET = tex("border_edge_top");
    private static final ResourceLocation TEX_EB = tex("border_edge_bottom");
    private static final ResourceLocation TEX_EL = tex("border_edge_left");
    private static final ResourceLocation TEX_ER = tex("border_edge_right");

    private static final int BORDER = 5;
    private static final int FUEL_ROW_H = 18;
    private static final int TOGGLE_H = 18;
    private static final int LABEL_H = 8;
    private static final int ENTRY_H = 20;
    private static final int MAX_ENTRIES = 3;
    private static final int INPUT_H = 20;
    private static final int SPLIT_H = 14;
    private static final int PLAYER_INV_H = 76;

    private int entryStartY;
    private int splitY;
    private int playerInvTop;

    private Button hostileBtn;
    private Button passiveBtn;
    private EditBox nameInput;
    private final List<Button> removeButtons = new ArrayList<>();

    private Button particlesBtn;

    private String labelParticles() {
        return "Particles:" + (menu.particlesEnabled ? "ON" : "OFF");
    }

    public CenterFuelScreen(CenterFuelMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        int contentH = FUEL_ROW_H + 2 + TOGGLE_H + 2 + LABEL_H + 2 + MAX_ENTRIES * ENTRY_H + 2 + INPUT_H + 4 + SPLIT_H + PLAYER_INV_H;
        this.imageHeight = BORDER + contentH + BORDER;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos;
        int y = this.topPos;

        entryStartY = y + BORDER + FUEL_ROW_H + 2 + TOGGLE_H + 2 + LABEL_H + 2;
        int inputY = entryStartY + MAX_ENTRIES * ENTRY_H + 2;
        splitY = inputY + INPUT_H + 4;
        playerInvTop = splitY + SPLIT_H;

        int toggleW = 40;
        int toggleX = x + (this.imageWidth - toggleW * 2 - 4) / 2;
        int toggleY = y + BORDER + FUEL_ROW_H + 2;

        hostileBtn = addRenderableWidget(Button.builder(
                Component.literal(labelHostile()), b -> {
                    PacketDistributor.sendToServer(new ServerboundCenterSettingsPayload(
                            menu.centerPos, "toggle_hostile", ""));
                    menu.allowHostileMobs = !menu.allowHostileMobs;
                    b.setMessage(Component.literal(labelHostile()));
                })
                .bounds(toggleX, toggleY, toggleW, TOGGLE_H)
                .build());

        passiveBtn = addRenderableWidget(Button.builder(
                Component.literal(labelPassive()), b -> {
                    PacketDistributor.sendToServer(new ServerboundCenterSettingsPayload(
                            menu.centerPos, "toggle_passive", ""));
                    menu.allowPassiveMobs = !menu.allowPassiveMobs;
                    b.setMessage(Component.literal(labelPassive()));
                })
                .bounds(toggleX + toggleW + 4, toggleY, toggleW, TOGGLE_H)
                .build());

        particlesBtn = addRenderableWidget(Button.builder(
                        Component.literal(labelParticles()), b -> {
                            PacketDistributor.sendToServer(new ServerboundCenterSettingsPayload(
                                    menu.centerPos, "toggle_particles", ""));
                            menu.particlesEnabled = !menu.particlesEnabled;
                            b.setMessage(Component.literal(labelParticles()));
                        })
                .bounds(toggleX + (toggleW / 2), toggleY + TOGGLE_H + 2, 60, TOGGLE_H)
                .build());

        int inputW = 110;
        int inputX = x + BORDER;
        nameInput = addRenderableWidget(new EditBox(this.font, inputX, inputY, inputW, INPUT_H - 4, Component.literal("")));

        addRenderableWidget(Button.builder(
                Component.translatable("gui.magic_ritual_mod.add"), b -> {
                    String name = nameInput.getValue().trim();
                    if (!name.isEmpty()) {
                        PacketDistributor.sendToServer(new ServerboundCenterSettingsPayload(
                                menu.centerPos, "add_blacklist", name));
                        menu.blacklistedPlayers.add(name);
                        nameInput.setValue("");
                        refreshRemoveButtons();
                    }
                })
                .bounds(inputX + inputW + 2, inputY - 2, 42, INPUT_H)
                .build());

        refreshRemoveButtons();
    }

    private void refreshRemoveButtons() {
        removeButtons.forEach(this::removeWidget);
        removeButtons.clear();

        int x = this.leftPos;
        int btnW = 46;
        int btnX = x + this.imageWidth - BORDER - btnW - 2;
        List<String> names = new ArrayList<>(menu.blacklistedPlayers);
        int count = Math.min(names.size(), MAX_ENTRIES);
        for (int i = 0; i < count; i++) {
            int idx = i;
            String playerName = names.get(i);
            Button removeBtn = addRenderableWidget(Button.builder(
                    Component.translatable("gui.magic_ritual_mod.remove"), b -> {
                        PacketDistributor.sendToServer(new ServerboundCenterSettingsPayload(
                                menu.centerPos, "remove_blacklist", playerName));
                        menu.blacklistedPlayers.remove(playerName);
                        refreshRemoveButtons();
                    })
                    .bounds(btnX, entryStartY + idx * ENTRY_H + 1, btnW, ENTRY_H - 2)
                    .build());
            removeButtons.add(removeBtn);
        }
    }

    private String labelHostile() {
        return "Hostile:" + (menu.allowHostileMobs ? "ON" : "OFF");
    }

    private String labelPassive() {
        return "Passive:" + (menu.allowPassiveMobs ? "ON" : "OFF");
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (nameInput.isFocused()) {
            if (nameInput.keyPressed(keyCode, scanCode, modifiers)) return true;
            if (keyCode == 256) {
                this.onClose();
                return true;
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (nameInput.isFocused() && nameInput.charTyped(codePoint, modifiers)) return true;
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mx, int my) {
        int x = this.leftPos, y = this.topPos;
        int W = this.imageWidth, H = this.imageHeight;

        g.blit(TEX_FILL, x + BORDER, y + BORDER, 0, 0, W - BORDER * 2, H - BORDER * 2, 1, 1);

        g.blit(TEX_TL, x, y, 0, 0, BORDER, BORDER, BORDER, BORDER);
        g.blit(TEX_TR, x + W - BORDER, y, 0, 0, BORDER, BORDER, BORDER, BORDER);
        g.blit(TEX_BL, x, y + H - BORDER, 0, 0, BORDER, BORDER, BORDER, BORDER);
        g.blit(TEX_BR, x + W - BORDER, y + H - BORDER, 0, 0, BORDER, BORDER, BORDER, BORDER);

        g.blit(TEX_ET, x + BORDER, y, 0, 0, W - BORDER * 2, BORDER, 1, BORDER);
        g.blit(TEX_EB, x + BORDER, y + H - BORDER, 0, 0, W - BORDER * 2, BORDER, 1, BORDER);
        g.blit(TEX_EL, x, y + BORDER, 0, 0, BORDER, H - BORDER * 2, BORDER, 1);
        g.blit(TEX_ER, x + W - BORDER, y + BORDER, 0, 0, BORDER, H - BORDER * 2, BORDER, 1);

        for (int i = 0; i < 1; i++) {
            var slot = this.menu.slots.get(i);
            g.blit(TEX_SLOT, x + slot.x - 1, y + slot.y - 1, 0, 0, 18, 18, 18, 18);
        }

        g.blit(TEX_SPLIT, x + BORDER, splitY, 0, 0, W - BORDER * 2, SPLIT_H, 1, SPLIT_H);

        int playerInvLeft = x + (W - 162) / 2;
        g.blit(TEX_PSLOT, playerInvLeft - 1, playerInvTop - 1, 0, 0, 162, PLAYER_INV_H, 162, PLAYER_INV_H);

        g.drawString(this.font, Component.translatable("gui.magic_ritual_mod.blacklist"),
                x + BORDER, entryStartY - LABEL_H - 2, 0x404040, false);

        List<String> names = new ArrayList<>(menu.blacklistedPlayers);
        int count = Math.min(names.size(), MAX_ENTRIES);
        for (int i = 0; i < count; i++) {
            g.drawString(this.font, " - " + names.get(i),
                    x + BORDER + 2, entryStartY + i * ENTRY_H + 6, 0x404040, false);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {
        g.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        g.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }
}

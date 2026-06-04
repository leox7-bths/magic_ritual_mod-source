package com.example.magic_ritual_mod.ui;

import com.example.magic_ritual_mod.block.custom.CircleType;
import com.example.magic_ritual_mod.network.payload.ServerboundPlaceCirclePayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class CircleSelectionScreen extends Screen {
    protected CircleSelectionScreen() {
        super(Component.translatable("gui.magic_ritual_mod.select_circle"));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = width / 2;
        int centerY = height / 2;
        int bw = 120;
        int bh = 20;
        int gap = 5;

        addRenderableWidget(new Button.Builder(
                Component.translatable("circle_type.magic_ritual_mod.attack"),
                btn -> selectType(CircleType.ATTACK)
        ).bounds(centerX - bw / 2, centerY - bh - gap, bw, bh).build());

        addRenderableWidget(new Button.Builder(
                Component.translatable("circle_type.magic_ritual_mod.defense"),
                btn -> selectType(CircleType.DEFENSE)
        ).bounds(centerX - bw / 2, centerY, bw, bh).build());

        addRenderableWidget(new Button.Builder(
                Component.translatable("circle_type.magic_ritual_mod.regeneration"),
                btn -> selectType(CircleType.REGENERATION)
        ).bounds(centerX - bw / 2, centerY + bh + gap, bw, bh).build());
    }

    private void selectType(CircleType type) {
        PacketDistributor.sendToServer(new ServerboundPlaceCirclePayload(type.getSerializedName()));
        onClose();
    }
}

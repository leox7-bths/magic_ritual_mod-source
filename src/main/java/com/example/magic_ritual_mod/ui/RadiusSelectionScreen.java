package com.example.magic_ritual_mod.ui;

import com.example.magic_ritual_mod.network.payload.ServerboundPlaceCenterPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class RadiusSelectionScreen extends Screen {
    protected RadiusSelectionScreen() {
        super(Component.translatable("gui.magic_ritual_mod.select_radius"));
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
                Component.translatable("radius.magic_ritual_mod.r3"),
                btn -> selectRadius(3)
        ).bounds(centerX - bw / 2, centerY - (bh + gap) * 3, bw, bh).build());

        addRenderableWidget(new Button.Builder(
                Component.translatable("radius.magic_ritual_mod.r5"),
                btn -> selectRadius(5)
        ).bounds(centerX - bw / 2, centerY - (bh + gap) * 2, bw, bh).build());

        addRenderableWidget(new Button.Builder(
                Component.translatable("radius.magic_ritual_mod.r7"),
                btn -> selectRadius(7)
        ).bounds(centerX - bw / 2, centerY - (bh + gap), bw, bh).build());

        addRenderableWidget(new Button.Builder(
                Component.translatable("radius.magic_ritual_mod.r9"),
                btn -> selectRadius(9)
        ).bounds(centerX - bw / 2, centerY, bw, bh).build());

        addRenderableWidget(new Button.Builder(
                Component.translatable("radius.magic_ritual_mod.r11"),
                btn -> selectRadius(11)
        ).bounds(centerX - bw / 2, centerY + (bh + gap), bw, bh).build());

        addRenderableWidget(new Button.Builder(
                Component.translatable("radius.magic_ritual_mod.r13"),
                btn -> selectRadius(13)
        ).bounds(centerX - bw / 2, centerY + (bh + gap) * 2, bw, bh).build());
    }

    private void selectRadius(int radius) {
        PacketDistributor.sendToServer(new ServerboundPlaceCenterPayload(radius));
        onClose();
    }
}

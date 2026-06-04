package com.example.magic_ritual_mod.ui;

import com.example.magic_ritual_mod.MagicRitualMod;
import com.example.magic_ritual_mod.effect.ModEffects;
import com.example.magic_ritual_mod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = MagicRitualMod.MODID, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.CENTER_FUEL.get(), CenterFuelScreen::new);
    }

    @SubscribeEvent
    public static void onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isUseItem() || event.getHand() != InteractionHand.MAIN_HAND) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!mc.player.getMainHandItem().is(ModItems.RITUAL_DAGGER.get())) return;

        event.setCanceled(true);
        if (Screen.hasShiftDown()) {
            mc.setScreen(new RadiusSelectionScreen());
        } else {
            mc.setScreen(new CircleSelectionScreen());
        }
    }

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        if (!(event.getCamera().getEntity() instanceof LivingEntity living)) return;
        if (!living.hasEffect(ModEffects.FOG)) return;
        event.setRed(1.0F);
        event.setGreen(1.0F);
        event.setBlue(1.0F);
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (!(event.getCamera().getEntity() instanceof LivingEntity living)) return;
        if (!living.hasEffect(ModEffects.FOG)) return;
        event.setNearPlaneDistance(0.0F);
        event.setFarPlaneDistance(5.0F);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (!event.getEntity().hasEffect(ModEffects.INVERTED_CONTROL)) return;
        Input input = event.getInput();
        input.leftImpulse = -input.leftImpulse;
        input.forwardImpulse = -input.forwardImpulse;
    }
}

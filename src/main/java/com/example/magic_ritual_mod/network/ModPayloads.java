package com.example.magic_ritual_mod.network;

import com.example.magic_ritual_mod.MagicRitualMod;
import com.example.magic_ritual_mod.block.ModBlocks;
import com.example.magic_ritual_mod.block.custom.CircleType;
import com.example.magic_ritual_mod.block.custom.MagicCenterBlock;
import com.example.magic_ritual_mod.block.custom.MagicCenterBlockEntity;
import com.example.magic_ritual_mod.block.custom.MagicCircleBlock;
import com.example.magic_ritual_mod.effect.ModEffects;
import com.example.magic_ritual_mod.network.payload.ServerboundCenterSettingsPayload;
import com.example.magic_ritual_mod.network.payload.ServerboundPlaceCenterPayload;
import com.example.magic_ritual_mod.network.payload.ServerboundPlaceCirclePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MagicRitualMod.MODID)
public class ModPayloads {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                ServerboundPlaceCirclePayload.TYPE,
                ServerboundPlaceCirclePayload.STREAM_CODEC,
                ModPayloads::handlePlaceCircle
        );

        registrar.playToServer(
                ServerboundPlaceCenterPayload.TYPE,
                ServerboundPlaceCenterPayload.STREAM_CODEC,
                ModPayloads::handlePlaceCenter
        );

        registrar.playToServer(
                ServerboundCenterSettingsPayload.TYPE,
                ServerboundCenterSettingsPayload.STREAM_CODEC,
                ModPayloads::handleCenterSettings
        );
    }

    private static void handlePlaceCircle(ServerboundPlaceCirclePayload payload,
                                           net.neoforged.neoforge.network.handling.IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        CircleType type = CircleType.valueOf(payload.circleType().toUpperCase());

        var level = player.serverLevel();
        var placePos = player.blockPosition();

        if (level.getBlockState(placePos).isAir()) {
            level.setBlock(placePos,
                    ModBlocks.MAGIC_CIRCLE_BLOCK.get().defaultBlockState()
                            .setValue(MagicCircleBlock.CIRCLE_TYPE, type),
                    3);
        }

        player.addEffect(new MobEffectInstance(ModEffects.BLEEDING, 60, 0, false, true, true));
    }

    private static void handlePlaceCenter(ServerboundPlaceCenterPayload payload,
                                           net.neoforged.neoforge.network.handling.IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        int radius = payload.radius();

        var level = player.serverLevel();
        var placePos = player.blockPosition();

        if (level.getBlockState(placePos).isAir()) {
            level.setBlock(placePos,
                    ModBlocks.MAGIC_CENTER_BLOCK.get().defaultBlockState()
                            .setValue(MagicCenterBlock.RADIUS, radius),
                    3);
        }

        int bleedingTicks = radius * 20;
        player.addEffect(new MobEffectInstance(ModEffects.BLEEDING, bleedingTicks, 0, false, true, true));
    }

    private static void handleCenterSettings(ServerboundCenterSettingsPayload payload,
                                              net.neoforged.neoforge.network.handling.IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        Level level = player.serverLevel();
        BlockEntity be = level.getBlockEntity(payload.pos());
        if (!(be instanceof MagicCenterBlockEntity mcbe)) return;

        switch (payload.action()) {
            case "toggle_hostile" -> mcbe.setAllowHostileMobs(!mcbe.isAllowHostileMobs());
            case "toggle_passive" -> mcbe.setAllowPassiveMobs(!mcbe.isAllowPassiveMobs());
            case "add_blacklist" -> mcbe.addBlacklistedPlayer(payload.value());
            case "remove_blacklist" -> mcbe.removeBlacklistedPlayer(payload.value());
            case "toggle_particles" -> {
                mcbe.setParticlesEnabled(!mcbe.isParticlesEnabled());
                mcbe.setChanged();
                level.sendBlockUpdated(
                        mcbe.getBlockPos(),
                        mcbe.getBlockState(),
                        mcbe.getBlockState(),
                        3
                );
            }        }
    }
}

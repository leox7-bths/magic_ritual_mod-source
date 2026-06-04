package com.example.magic_ritual_mod.network.payload;

import com.example.magic_ritual_mod.MagicRitualMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ServerboundPlaceCenterPayload(int radius) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundPlaceCenterPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MagicRitualMod.MODID, "place_center"));

    public static final StreamCodec<ByteBuf, ServerboundPlaceCenterPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ServerboundPlaceCenterPayload::radius,
                    ServerboundPlaceCenterPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

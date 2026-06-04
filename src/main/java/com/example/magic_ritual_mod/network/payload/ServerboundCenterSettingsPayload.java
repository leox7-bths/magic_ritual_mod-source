package com.example.magic_ritual_mod.network.payload;

import com.example.magic_ritual_mod.MagicRitualMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ServerboundCenterSettingsPayload(BlockPos pos, String action, String value) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundCenterSettingsPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MagicRitualMod.MODID, "center_settings"));

    public static final StreamCodec<ByteBuf, ServerboundCenterSettingsPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ServerboundCenterSettingsPayload::pos,
                    ByteBufCodecs.STRING_UTF8, ServerboundCenterSettingsPayload::action,
                    ByteBufCodecs.STRING_UTF8, ServerboundCenterSettingsPayload::value,
                    ServerboundCenterSettingsPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

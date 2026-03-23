package com.kahootmod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.kahootmod.KahootMod;

public record AnswerPacket(int answerIndex) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<AnswerPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(KahootMod.MODID, "answer"));

    public static final StreamCodec<FriendlyByteBuf, AnswerPacket> STREAM_CODEC = StreamCodec.composite(
        net.minecraft.network.codec.ByteBufCodecs.VAR_INT, AnswerPacket::answerIndex,
        AnswerPacket::new
    );

    @Override
    public CustomPacketPayload.Type<AnswerPacket> type() {
        return TYPE;
    }
}

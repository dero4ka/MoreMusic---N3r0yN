package com.example.fardisc.network;

import com.example.fardisc.FarDisc;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Рассылается сервером всем игрокам, когда кто-то включает или
 * выключает колонку, чтобы её услышали остальные, а не только тот,
 * кто нажал ПКМ.
 */
public record SpeakerPlaybackPayload(String url, boolean playing) implements CustomPacketPayload {

    public static final Type<SpeakerPlaybackPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(FarDisc.MOD_ID, "speaker_playback"));

    public static final StreamCodec<ByteBuf, SpeakerPlaybackPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(500), SpeakerPlaybackPayload::url,
            ByteBufCodecs.BOOL, SpeakerPlaybackPayload::playing,
            SpeakerPlaybackPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package com.example.fardisc.network;

import com.example.fardisc.FarDisc;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Отправляется с клиента на сервер, когда игрок сохраняет ссылку в окне
 * настройки колонки (SpeakerUrlScreen).
 */
public record SpeakerUrlPayload(String url) implements CustomPacketPayload {

    public static final Type<SpeakerUrlPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(FarDisc.MOD_ID, "speaker_url"));

    public static final StreamCodec<ByteBuf, SpeakerUrlPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(500), SpeakerUrlPayload::url,
            SpeakerUrlPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

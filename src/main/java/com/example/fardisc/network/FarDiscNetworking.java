package com.example.fardisc.network;

import com.example.fardisc.FarDisc;
import com.example.fardisc.client.SpeakerAudioPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = FarDisc.MOD_ID)
public class FarDiscNetworking {

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(SpeakerUrlPayload.TYPE, SpeakerUrlPayload.STREAM_CODEC,
                FarDiscNetworking::handleSpeakerUrl);

        registrar.playToClient(SpeakerPlaybackPayload.TYPE, SpeakerPlaybackPayload.STREAM_CODEC,
                FarDiscNetworking::handleSpeakerPlayback);
    }

    // Клиент -> сервер: игрок сохранил новую ссылку в окне колонки.
    private static void handleSpeakerUrl(SpeakerUrlPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ItemStack stack = player.getMainHandItem();
            if (stack.is(FarDisc.SPEAKER.get())) {
                stack.set(FarDisc.SPEAKER_URL.get(), payload.url());
            }
        });
    }

    // Сервер -> клиент: кто-то включил/выключил колонку, всем нужно
    // запустить/остановить тот же поток у себя.
    private static void handleSpeakerPlayback(SpeakerPlaybackPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.playing()) {
                SpeakerAudioPlayer.start(payload.url());
            } else {
                SpeakerAudioPlayer.stop();
            }
        });
    }
}

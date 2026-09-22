package com.example.fardisc.client;

import com.example.fardisc.FarDisc;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.PlayLevelSoundEvent;

/**
 * Перехватывает момент, когда движок собирается проиграть звук нашей
 * пластинки (fardisc:music_disc.far), отменяет обычное позиционное
 * воспроизведение и вместо него запускает FarDiscSoundInstance —
 * версию без затухания и без панорамирования по наушникам.
 *
 * Событие PlayLevelSoundEvent.AtPosition вызывается из
 * Level#playSeededSound, через который проигрывается звук
 * музыкальной пластинки (см. JukeboxSongPlayer).
 */
@EventBusSubscriber(modid = FarDisc.MOD_ID, value = Dist.CLIENT)
public class FarDiscClientEvents {

    // 72.3 секунды * 20 тиков/сек, округлено вверх.
    private static final int FAR_DURATION_TICKS = 1446;

    private static FarDiscSoundInstance activeInstance = null;

    @SubscribeEvent
    public static void onPlayLevelSound(PlayLevelSoundEvent.AtPosition event) {
        Holder<SoundEvent> sound = event.getSound();
        if (sound == null) {
            return;
        }
        ResourceLocation id = sound.value().getLocation();
        if (!id.equals(ResourceLocation.fromNamespaceAndPath(FarDisc.MOD_ID, "music_disc.far"))) {
            return;
        }

        // Отменяем обычное позиционное воспроизведение...
        event.setCanceled(true);

        // ...и запускаем своё, всегда слышимое ровно посередине.
        if (activeInstance != null) {
            activeInstance.stopEarly();
        }
        activeInstance = new FarDiscSoundInstance(
                sound,
                event.getSource(),
                event.getNewVolume(),
                event.getNewPitch(),
                event.getPosition().x, event.getPosition().y, event.getPosition().z,
                FAR_DURATION_TICKS
        );
        Minecraft.getInstance().getSoundManager().play(activeInstance);
    }
}

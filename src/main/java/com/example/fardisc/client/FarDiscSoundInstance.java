package com.example.fardisc.client;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

/**
 * Проигрывает трек пластинки как "относительный" звук: без затухания по
 * расстоянию и без панорамирования между наушниками — он всегда звучит
 * ровно по центру, пока слушатель остаётся в радиусе слышимости
 * (16 блоков, как обычная запись).
 */
public class FarDiscSoundInstance extends AbstractTickableSoundInstance {

    private static final int HEARING_RANGE_BLOCKS = 16;
    private final int durationTicks;
    private int ticksPlayed = 0;
    private boolean stoppedEarly = false;

    public FarDiscSoundInstance(Holder<SoundEvent> sound, SoundSource source,
                                 float volume, float pitch,
                                 double x, double y, double z,
                                 int durationTicks) {
        super(sound.value(), source, net.minecraft.util.RandomSource.create());
        this.volume = volume;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.durationTicks = durationTicks;

        // Ключевая часть: relative=true + Attenuation.NONE.
        // relative вместе с NONE-затуханием заставляет звуковой движок
        // трактовать источник как "слушательный", то есть всегда центрированный
        // и на постоянной громкости, а не позиционированный в 3D-пространстве.
        this.relative = true;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.looping = false;
    }

    public void stopEarly() {
        this.stoppedEarly = true;
    }

    @Override
    public void tick() {
        ticksPlayed++;
        if (stoppedEarly || ticksPlayed >= durationTicks) {
            this.stop();
        }
    }
}

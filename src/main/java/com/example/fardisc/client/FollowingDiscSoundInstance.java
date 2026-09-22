package com.example.fardisc.client;

import com.example.fardisc.FarDisc;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Звук, который каждый тик пересчитывает свою позицию по текущим
 * координатам сущности, несущей портативный проигрыватель — то есть
 * "следует" за игроком, пока тот держит включённый плеер.
 *
 * Обычное затухание по расстоянию и панорамирование НЕ отключены
 * (в отличие от FarDiscSoundInstance для блока-проигрывателя): это
 * портативная колонка, её должны слышать и другие игроки поблизости.
 */
public class FollowingDiscSoundInstance extends AbstractTickableSoundInstance {

    private final LivingEntity carrier;
    private final InteractionHand hand;

    public FollowingDiscSoundInstance(Holder<SoundEvent> sound, LivingEntity carrier, InteractionHand hand) {
        super(sound.value(), SoundSource.RECORDS, RandomSource.create());
        this.carrier = carrier;
        this.hand = hand;
        this.looping = true;
        this.volume = 1.0F;
        this.pitch = 1.0F;
        this.x = carrier.getX();
        this.y = carrier.getY();
        this.z = carrier.getZ();
    }

    @Override
    public void tick() {
        if (!carrier.isAlive()) {
            this.stop();
            return;
        }

        ItemStack stack = carrier.getItemInHand(hand);
        boolean stillValid = stack.is(FarDisc.PORTABLE_DISC_PLAYER.get())
                && Boolean.TRUE.equals(stack.get(FarDisc.DISC_INSERTED.get()))
                && Boolean.TRUE.equals(stack.get(FarDisc.PLAYING.get()));

        if (!stillValid) {
            this.stop();
            return;
        }

        this.x = carrier.getX();
        this.y = carrier.getY();
        this.z = carrier.getZ();
    }
}

package com.example.fardisc.client;

import com.example.fardisc.FarDisc;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Раз в тик проверяет, у кого из игроков в руке включённый портативный
 * проигрыватель со вставленным диском, и запускает/останавливает для
 * них FollowingDiscSoundInstance. Работает для всех игроков, которых
 * видит клиент (их предметы в руках синхронизируются игрой как обычно),
 * поэтому звук слышен и другим игрокам рядом, а не только владельцу.
 */
@EventBusSubscriber(modid = FarDisc.MOD_ID, value = Dist.CLIENT)
public class PortableDiscPlayerClientEvents {

    // Ключ = id сущности * 2 + номер руки (0 = основная, 1 = вторая).
    private static final Map<Integer, FollowingDiscSoundInstance> ACTIVE = new HashMap<>();

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            if (!ACTIVE.isEmpty()) {
                ACTIVE.clear();
            }
            return;
        }

        Set<Integer> stillPlaying = new HashSet<>();

        for (Player player : mc.level.players()) {
            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stack = player.getItemInHand(hand);
                boolean active = stack.is(FarDisc.PORTABLE_DISC_PLAYER.get())
                        && Boolean.TRUE.equals(stack.get(FarDisc.DISC_INSERTED.get()))
                        && Boolean.TRUE.equals(stack.get(FarDisc.PLAYING.get()));

                if (!active) {
                    continue;
                }

                int key = player.getId() * 2 + hand.ordinal();
                stillPlaying.add(key);

                if (!ACTIVE.containsKey(key)) {
                    FollowingDiscSoundInstance instance =
                            new FollowingDiscSoundInstance(FarDisc.FAR_SOUND, player, hand);
                    ACTIVE.put(key, instance);
                    mc.getSoundManager().play(instance);
                }
            }
        }

        // Убираем записи для звуков, которые уже остановились сами
        // (диск извлекли, пауза, игрок ушёл далеко и т.д.) — их
        // остановку делает FollowingDiscSoundInstance.tick() сам по себе,
        // здесь только чистим карту, чтобы не копилась.
        ACTIVE.keySet().removeIf(key -> !stillPlaying.contains(key));
    }
}

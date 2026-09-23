package com.example.fardisc.item;

import com.example.fardisc.FarDisc;
import com.example.fardisc.client.SpeakerUrlScreen;
import com.example.fardisc.network.SpeakerPlaybackPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

/**
 * Колонка. Ссылка на поток хранится в самом предмете (своя у каждой
 * колонки), задаётся игроком прямо в игре.
 *
 * Правый клик:
 *  - Shift + ПКМ -> открыть окно ввода ссылки (только у себя на экране).
 *  - Обычный ПКМ -> играть/пауза. Состояние меняет сервер и рассылает
 *    его игрокам в радиусе {@link #HEARING_RADIUS} блоков от того, кто
 *    нажал (на момент нажатия), поэтому звук слышен не только ему.
 *
 * Ограничение: звук всё ещё не позиционный (не через звуковой движок
 * игры) и не подстраивается под то, кто сколько времени слушает —
 * это по-прежнему обычный локальный плеер на каждом клиенте, просто
 * запускаемый по общей команде.
 */
public class SpeakerItem extends Item {

    // Дальность слышимости колонки.
    private static final double HEARING_RADIUS = 32.0;

    public SpeakerItem(Properties properties) {
        super(properties);
    }

    private static String getUrl(ItemStack stack) {
        return stack.getOrDefault(FarDisc.SPEAKER_URL.get(), "");
    }

    private static boolean isPlaying(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(FarDisc.PLAYING.get()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            if (level.isClientSide) {
                Minecraft.getInstance().setScreen(new SpeakerUrlScreen(getUrl(stack)));
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        if (!level.isClientSide) {
            boolean nowPlaying = !isPlaying(stack);
            stack.set(FarDisc.PLAYING.get(), nowPlaying);
            PacketDistributor.sendToPlayersNear(
                    (ServerLevel) level,
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    HEARING_RADIUS,
                    new SpeakerPlaybackPayload(getUrl(stack), nowPlaying));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String url = getUrl(stack);
        tooltip.add((url.isEmpty()
                ? Component.translatable("item.fardisc.speaker.no_url")
                : Component.literal(url))
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(isPlaying(stack)
                ? "item.fardisc.portable_disc_player.playing"
                : "item.fardisc.portable_disc_player.paused")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.fardisc.speaker.usage").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.fardisc.speaker.usage_set").withStyle(ChatFormatting.DARK_GRAY));
    }
}

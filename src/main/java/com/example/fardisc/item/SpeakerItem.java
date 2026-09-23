package com.example.fardisc.item;

import com.example.fardisc.FarDisc;
import com.example.fardisc.client.SpeakerAudioPlayer;
import com.example.fardisc.client.SpeakerUrlScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Колонка. Ссылка на поток хранится в самом предмете (своя у каждой
 * колонки), задаётся игроком прямо в игре.
 *
 * Правый клик:
 *  - Shift + ПКМ -> открыть окно ввода ссылки.
 *  - Обычный ПКМ -> играть/пауза по уже сохранённой ссылке.
 *
 * Воспроизведение идёт только клиентской стороной через
 * SpeakerAudioPlayer. Сама ссылка при этом хранится в компоненте
 * предмета (обычные данные, синхронизируются как всегда), поэтому
 * сохраняется между сессиями.
 */
public class SpeakerItem extends Item {

    public SpeakerItem(Properties properties) {
        super(properties);
    }

    private static String getUrl(ItemStack stack) {
        return stack.getOrDefault(FarDisc.SPEAKER_URL.get(), "");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            if (player.isShiftKeyDown()) {
                Minecraft.getInstance().setScreen(new SpeakerUrlScreen(getUrl(stack)));
            } else if (SpeakerAudioPlayer.isPlaying()) {
                SpeakerAudioPlayer.stop();
            } else {
                SpeakerAudioPlayer.start(getUrl(stack));
            }
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
        tooltip.add(Component.translatable("item.fardisc.speaker.usage").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("item.fardisc.speaker.usage_set").withStyle(ChatFormatting.DARK_GRAY));
    }
}

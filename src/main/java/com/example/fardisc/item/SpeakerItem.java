package com.example.fardisc.item;

import com.example.fardisc.FarDiscConfig;
import com.example.fardisc.client.SpeakerAudioPlayer;
import net.minecraft.ChatFormatting;
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
 * Колонка. Правый клик включает/выключает интернет-радио по ссылке
 * из настроек мода (config/fardisc-common.toml -> speaker.streamUrl).
 *
 * Само воспроизведение идёт клиентской стороной через SpeakerAudioPlayer,
 * поэтому на сервере просто ничего не делаем.
 */
public class SpeakerItem extends Item {

    public SpeakerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            if (SpeakerAudioPlayer.isPlaying()) {
                SpeakerAudioPlayer.stop();
            } else {
                SpeakerAudioPlayer.start(FarDiscConfig.STREAM_URL.get());
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.fardisc.speaker.usage").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.fardisc.speaker.note").withStyle(ChatFormatting.DARK_GRAY));
    }
}

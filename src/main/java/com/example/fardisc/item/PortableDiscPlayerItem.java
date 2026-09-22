package com.example.fardisc.item;

import com.example.fardisc.FarDisc;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

/**
 * Портативный проигрыватель. Носится в руке и играет вставленную
 * пластинку Far, пока не будет поставлен на паузу или пока диск не
 * будет извлечён.
 *
 * Управление (правый клик предметом в руке):
 *  - Пусто внутри + во второй руке пластинка Far -> вставить и начать играть.
 *  - Есть диск, играет -> пауза.
 *  - Есть диск, на паузе -> продолжить.
 *  - Shift + правый клик, есть диск -> извлечь диск обратно в инвентарь.
 */
public class PortableDiscPlayerItem extends Item {

    public PortableDiscPlayerItem(Properties properties) {
        super(properties);
    }

    private static boolean hasDisc(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(FarDisc.DISC_INSERTED.get()));
    }

    private static boolean isPlaying(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(FarDisc.PLAYING.get()));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Извлечь диск.
        if (player.isShiftKeyDown() && hasDisc(stack)) {
            stack.set(FarDisc.DISC_INSERTED.get(), false);
            stack.set(FarDisc.PLAYING.get(), false);
            if (!level.isClientSide) {
                ItemStack disc = new ItemStack(FarDisc.MUSIC_DISC_FAR.get());
                if (!player.getInventory().add(disc)) {
                    player.drop(disc, false);
                }
            }
            return InteractionResult.SUCCESS;
        }

        // Вставить диск из второй руки.
        if (!hasDisc(stack)) {
            InteractionHand otherHand = hand == InteractionHand.MAIN_HAND
                    ? InteractionHand.OFF_HAND
                    : InteractionHand.MAIN_HAND;
            ItemStack otherStack = player.getItemInHand(otherHand);

            if (otherStack.is(FarDisc.MUSIC_DISC_FAR.get())) {
                stack.set(FarDisc.DISC_INSERTED.get(), true);
                stack.set(FarDisc.PLAYING.get(), true);
                if (!level.isClientSide) {
                    otherStack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        // Пауза / продолжить.
        stack.set(FarDisc.PLAYING.get(), !isPlaying(stack));
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (hasDisc(stack)) {
            tooltip.add(Component.translatable(isPlaying(stack)
                    ? "item.fardisc.portable_disc_player.playing"
                    : "item.fardisc.portable_disc_player.paused"));
        } else {
            tooltip.add(Component.translatable("item.fardisc.portable_disc_player.empty"));
        }
    }
}

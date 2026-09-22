package com.example.fardisc;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(FarDisc.MOD_ID)
public class FarDisc {
    public static final String MOD_ID = "fardisc";

    // Звук пластинки (файл: assets/fardisc/sounds/music_disc/far.ogg, описан в sounds.json)
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> FAR_SOUND = SOUNDS.register(
            "music_disc.far",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(MOD_ID, "music_disc.far")));

    // Ключ песни для проигрывателя (описание в data/fardisc/jukebox_song/far.json)
    public static final ResourceKey<JukeboxSong> FAR_SONG = ResourceKey.create(
            Registries.JUKEBOX_SONG,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "far"));

    // Предметы
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<Item> MUSIC_DISC_FAR = ITEMS.register(
            "music_disc_far",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .jukeboxPlayable(FAR_SONG)));

    // Портативный проигрыватель. Пока умеет играть только пластинку Far.
    public static final DeferredItem<Item> PORTABLE_DISC_PLAYER = ITEMS.register(
            "portable_disc_player",
            () -> new com.example.fardisc.item.PortableDiscPlayerItem(new Item.Properties().stacksTo(1)));

    // Компоненты данных предмета: вставлен ли диск и играет ли он сейчас.
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> DISC_INSERTED =
            DATA_COMPONENTS.register("disc_inserted", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> PLAYING =
            DATA_COMPONENTS.register("playing", () -> DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build());

    // Своя вкладка креатива с иконкой-пластинкой
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MORE_MUSIC_TAB = TABS.register(
            "more_music",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MOD_ID))
                    .icon(() -> new ItemStack(MUSIC_DISC_FAR.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(MUSIC_DISC_FAR.get());
                        output.accept(PORTABLE_DISC_PLAYER.get());
                    })
                    .build());

    public FarDisc(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
        ITEMS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        TABS.register(modEventBus);
    }
}

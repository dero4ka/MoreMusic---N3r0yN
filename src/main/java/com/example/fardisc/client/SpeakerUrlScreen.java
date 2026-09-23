package com.example.fardisc.client;

import com.example.fardisc.network.SpeakerUrlPayload;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Открывается по Shift + ПКМ колонкой. Позволяет ввести ссылку на
 * MP3-поток прямо в игре. При сохранении отправляет её на сервер
 * (SpeakerUrlPayload), сервер прописывает её в предмет в руке.
 */
public class SpeakerUrlScreen extends Screen {

    private final String initialUrl;
    private EditBox urlBox;

    public SpeakerUrlScreen(String initialUrl) {
        super(Component.translatable("item.fardisc.speaker.set_url"));
        this.initialUrl = initialUrl == null ? "" : initialUrl;
    }

    @Override
    protected void init() {
        int boxWidth = Math.min(300, this.width - 40);

        this.urlBox = new EditBox(this.font,
                this.width / 2 - boxWidth / 2, this.height / 2 - 10,
                boxWidth, 20,
                Component.translatable("item.fardisc.speaker.url_field"));
        this.urlBox.setMaxLength(500);
        this.urlBox.setValue(initialUrl);
        this.addRenderableWidget(this.urlBox);
        this.setInitialFocus(this.urlBox);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> save())
                .bounds(this.width / 2 - 100, this.height / 2 + 20, 200, 20)
                .build());
    }

    private void save() {
        PacketDistributor.sendToServer(new SpeakerUrlPayload(this.urlBox.getValue().trim()));
        this.onClose();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(null);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

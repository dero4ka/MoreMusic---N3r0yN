package com.example.fardisc;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Настройка мода. URL потока задаётся здесь и читается из
 * config/fardisc-client.toml после первого запуска игры с модом.
 *
 * Подходит прямая ссылка на MP3-поток Icecast/Shoutcast
 * (обычно вида http://адрес:порт/поток или .mp3 в конце).
 * Спотify и YouTube так не работают — это должен быть именно
 * прямой аудиопоток, а не ссылка на страницу сайта.
 */
public class FarDiscConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.ConfigValue<String> STREAM_URL;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("speaker");
        STREAM_URL = builder
                .comment("Прямая ссылка на MP3-поток интернет-радио (Icecast/Shoutcast).",
                        "Пример: http://ice1.somewhere.example:8000/stream.mp3")
                .define("streamUrl", "");
        builder.pop();

        SPEC = builder.build();
    }
}

package com.example.fardisc.client;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;

/**
 * Проигрывает MP3-поток (интернет-радио) в отдельном потоке, минуя
 * звуковой движок Minecraft — звук идёт через системную звуковую карту,
 * как будто открыт сторонний плеер. Поэтому он НЕ привязан к позиции
 * в игровом мире, не затухает с расстоянием и не учитывает игровые
 * ползунки громкости.
 *
 * Одновременно может играть только один поток: включение новой колонки
 * останавливает предыдущую. Пауза = разрыв соединения; "продолжить" =
 * новое подключение к потоку (для живого радио это нормально — вы
 * просто попадаете на текущий момент эфира, как в обычном радио).
 */
public class SpeakerAudioPlayer {

    private static volatile Player currentPlayer;
    private static volatile Thread currentThread;
    private static volatile boolean playing = false;

    public static synchronized boolean isPlaying() {
        return playing;
    }

    public static synchronized void start(String streamUrl) {
        stop();

        if (streamUrl == null || streamUrl.isBlank()) {
            System.err.println("[fardisc] URL потока не задан (config/fardisc-common.toml -> speaker.streamUrl)");
            return;
        }

        playing = true;
        currentThread = new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) URI.create(streamUrl).toURL().openConnection();
                connection.setRequestProperty("Icy-MetaData", "0");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(15000);

                Player player = new Player(connection.getInputStream());
                currentPlayer = player;
                player.play();
            } catch (IOException | JavaLayerException e) {
                System.err.println("[fardisc] Не удалось проиграть поток " + streamUrl + ": " + e.getMessage());
            } finally {
                playing = false;
            }
        }, "fardisc-speaker-stream");
        currentThread.setDaemon(true);
        currentThread.start();
    }

    public static synchronized void stop() {
        playing = false;
        if (currentPlayer != null) {
            currentPlayer.close();
            currentPlayer = null;
        }
        currentThread = null;
    }
}

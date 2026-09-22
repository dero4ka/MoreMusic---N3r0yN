# More Music - by N3r0yN (NeoForge 1.21.1)

Добавляет музыкальную пластинку `fardisc:music_disc_far`.

## Сборка
1. Установите JDK 21.
2. Скопируйте `gradlew`, `gradlew.bat` и папку `gradle/` из официального шаблона
   NeoForge MDK 1.21.1 (github.com/NeoForgeMDKs/MDK-1.21.1-ModDevGradle) в эту папку
   (или выполните `gradle wrapper`, если Gradle установлен).
3. `./gradlew build` -> готовый jar в `build/libs/`.
4. Тест в игре: `./gradlew runClient`.

## Трек
Сейчас на пластинке играет "Caution" (JackTheGen), 72.3 с.

## Смена трека
Замените `src/main/resources/assets/fardisc/sounds/music_disc/far.ogg`
на свой файл: формат OGG Vorbis, лучше моно.
Затем в `data/fardisc/jukebox_song/far.json` поставьте реальную длину
в секундах (`length_in_seconds`).

## Получение
`/give @p fardisc:music_disc_far` или креатив-вкладка "More Music - by N3r0yN".
Можно добавить в лут-таблицы через datapack/GLM.

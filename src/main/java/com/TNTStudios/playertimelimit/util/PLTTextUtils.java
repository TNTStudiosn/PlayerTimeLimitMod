package com.TNTStudios.playertimelimit.util;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PLTTextUtils {

    public static void sendMessage(ServerPlayerEntity player, String raw, int segundos) {
        String parsed = formatTime(raw, segundos);
        player.sendMessage(Text.of(parsed), false);
        player.playSound(
                net.minecraft.sound.SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(),
                player.getSoundCategory(),
                1.2f,
                1.0f
        );

    }


    public static String formatTime(String text, int segundos) {
        int horas = segundos / 3600;
        int minutos = (segundos % 3600) / 60;
        int seg = segundos % 60;

        return text.replace("%tiempo%", String.valueOf(segundos))
                .replace("%horas%", String.valueOf(horas))
                .replace("%minutos%", String.valueOf(minutos))
                .replace("%segundos%", String.valueOf(seg));
    }
}

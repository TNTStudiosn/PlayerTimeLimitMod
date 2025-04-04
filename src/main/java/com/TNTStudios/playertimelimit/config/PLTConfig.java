package com.TNTStudios.playertimelimit.config;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.io.FileWriter;

public class PLTConfig {

    public static class BossBarConfig {
        public String message = "Tiempo restante: %horas%h %minutos%m %segundos%s";
        public String color = "WHITE";
    }

    public static class Advertencia {
        public int tiempo; // en segundos
        public String mensaje;
    }

    public static class ReinicioConfig {
        public String hora = "00:00";
        public String zonaHoraria = "America/Mexico_City";
    }

    public static class Mensajes {
        public String tiempoAgotado = " Tu tiempo de juego para hoy ha terminado. ¡Nos vemos mañana!";
        public String tiempoRestante = "✨ Tienes %horas%h %minutos%m %segundos%s de juego restantes. ¡Disfruta!";
        public String tiempoAgregado = " ¡Se han agregado %horas%h %minutos%m %segundos%s a tu tiempo de juego!";
        public String tiempoRemovido = " Se han removido %horas%h %minutos%m %segundos%s de tu tiempo de juego.";
        public String tiempoRestablecido = " Tu tiempo de juego ha sido restablecido a %horas%h %minutos%m %segundos%s.";
        public String bienvenida = " ¡Bienvenido! Tienes %horas%h %minutos%m restantes de tiempo de juego por hoy ⏳. ¡Aprovecha al máximo cada momento!";
        public String pausado = "✨ Tu tiempo está actualmente pausado.";
    }

    public static BossBarConfig bossbar = new BossBarConfig();
    public static ReinicioConfig reinicio = new ReinicioConfig();
    public static List<Advertencia> advertencias = new ArrayList<>();
    public static int tiempoPorDefecto = 18000;
    public static Mensajes mensajes = new Mensajes();

    @SuppressWarnings("unchecked")
    public static void loadConfig() {
        try {
            File configFile = Paths.get("config", "playertimelimit.yaml").toFile();
            if (!configFile.exists()) {
                // Crear archivo con valores por defecto
                configFile.getParentFile().mkdirs();
                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write(defaultYaml());
                    System.out.println("[PlayerTimeLimit] Archivo de configuración creado por defecto.");
                }
                return;
            }

            org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml();
            Map<String, Object> root = yaml.load(new java.io.FileReader(configFile));

            Map<String, Object> bb = (Map<String, Object>) root.getOrDefault("bossbar", new HashMap<>());
            bossbar.message = (String) bb.getOrDefault("message", bossbar.message);
            bossbar.color = (String) bb.getOrDefault("color", bossbar.color);

            Map<String, Object> rein = (Map<String, Object>) root.getOrDefault("reinicio", new HashMap<>());
            reinicio.hora = (String) rein.getOrDefault("hora", reinicio.hora);
            reinicio.zonaHoraria = (String) rein.getOrDefault("zonaHoraria", reinicio.zonaHoraria);

            List<Map<String, Object>> advs = (List<Map<String, Object>>) root.getOrDefault("advertencias", new ArrayList<>());
            advertencias.clear();
            for (Map<String, Object> entry : advs) {
                Advertencia adv = new Advertencia();
                adv.tiempo = (int) entry.get("tiempo");
                adv.mensaje = (String) entry.get("mensaje");
                advertencias.add(adv);
            }

            tiempoPorDefecto = (int) root.getOrDefault("tiempoPorDefecto", tiempoPorDefecto);

            Map<String, Object> msgs = (Map<String, Object>) root.getOrDefault("mensajes", new HashMap<>());
            mensajes.tiempoAgotado = (String) msgs.getOrDefault("tiempoAgotado", mensajes.tiempoAgotado);
            mensajes.tiempoRestante = (String) msgs.getOrDefault("tiempoRestante", mensajes.tiempoRestante);
            mensajes.tiempoAgregado = (String) msgs.getOrDefault("tiempoAgregado", mensajes.tiempoAgregado);
            mensajes.tiempoRemovido = (String) msgs.getOrDefault("tiempoRemovido", mensajes.tiempoRemovido);
            mensajes.tiempoRestablecido = (String) msgs.getOrDefault("tiempoRestablecido", mensajes.tiempoRestablecido);
            mensajes.bienvenida = (String) msgs.getOrDefault("bienvenida", mensajes.bienvenida);
            mensajes.pausado = (String) msgs.getOrDefault("pausado", mensajes.pausado);

            System.out.println("[PlayerTimeLimit] Config cargada correctamente.");

        } catch (Exception e) {
            System.err.println("[PlayerTimeLimit] Error al cargar la configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static LocalTime getHoraReinicio() {
        try {
            return LocalTime.parse(reinicio.hora);
        } catch (Exception e) {
            return LocalTime.MIDNIGHT;
        }
    }

    public static ZoneId getZonaHoraria() {
        try {
            return ZoneId.of(reinicio.zonaHoraria);
        } catch (Exception e) {
            return ZoneId.of("UTC");
        }
    }

    private static String defaultYaml() {
        return """
# Configuración de TNTLimitTime
bossbar:
  message: "Tiempo restante: %horas%h %minutos%m %segundos%s"
  color: WHITE

reinicio:
  hora: "00:00"
  zonaHoraria: "America/Mexico_City"

advertencias:
  - tiempo: 1800
    mensaje: "⏳ Quedan 30 minutos de juego."
  - tiempo: 900
    mensaje: "⏳ Quedan 15 minutos de juego. ¡Aprovecha tu tiempo!"
  - tiempo: 120
    mensaje: "⏳ Quedan 2 minutos. ¡Prepárate para terminar!"

tiempoPorDefecto: 18000

mensajes:
  tiempoAgotado: " Tu tiempo de juego para hoy ha terminado. ¡Nos vemos mañana!"
  tiempoRestante: "✨ Tienes %horas%h %minutos%m %segundos%s de juego restantes. ¡Disfruta!"
  tiempoAgregado: " ¡Se han agregado %horas%h %minutos%m %segundos%s a tu tiempo de juego!"
  tiempoRemovido: " Se han removido %horas%h %minutos%m %segundos%s de tu tiempo de juego."
  tiempoRestablecido: " Tu tiempo de juego ha sido restablecido a %horas%h %minutos%m %segundos%s."
  bienvenida: " ¡Bienvenido! Tienes %horas%h %minutos%m restantes de tiempo de juego por hoy ⏳. ¡Aprovecha al máximo cada momento!"
  pausado: "✨ Tu tiempo está actualmente pausado."
""";
    }

}

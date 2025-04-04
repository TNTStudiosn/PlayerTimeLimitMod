# ⏱ PlayerTimeLimit

**PlayerTimeLimit** es un mod para servidores (no cliente) de Minecraft en Fabric que permite establecer límites de tiempo de juego por jugador, reiniciarlos diariamente, y gestionar estos tiempos a través de comandos y una API pública. Ideal para servidores educativos, familiares o con control de juego.

---

## 📌 Características

- ⏳ Tiempo diario configurable por jugador.
- ⚠️ Advertencias configurables previas al fin del tiempo.
- 📤 Kick automático al terminar el tiempo.
- 🔁 Reinicio diario automático basado en zona horaria.
- 💬 Mensajes personalizados configurables.
- 🧠 Soporte para jugadores offline (UUID cacheado).
- 📊 BossBar informativo en pantalla.
- 🔒 Soporte de permisos con **LuckPerms**.
- 📦 API pública para otros mods.

---

## 🧪 Comandos

Todos los comandos comienzan con `/plt` y soportan **autocompletado de jugadores**, incluso si están **offline**.

```bash
/plt check <jugador>            # Muestra el tiempo restante
/plt info <jugador>             # Muestra estado detallado (tiempo, pausado, agotado)
/plt addtime <jugador> <seg>    # Agrega tiempo al jugador
/plt removetime <jugador> <seg> # Remueve tiempo del jugador
/plt resettime <jugador>        # Reinicia el tiempo al valor por defecto
/plt pausar <jugador>           # Pausa el contador de tiempo del jugador
/plt reanudar <jugador>         # Reanuda el contador de tiempo
/plt reload                     # Recarga el archivo de configuración YAML
```
---

## 🔐 Permisos LuckPerms

Cada comando requiere permisos específicos que puedes asignar con plugins como LuckPerms:

| Permiso                             | Descripción                          |
|-------------------------------------|--------------------------------------|
| `PLimitTime.admin`                  | Acceso a todos los comandos          |
| `PLimitTime.command.check`          | Ver tiempo e info de jugadores       |
| `PLimitTime.command.addtime`        | Añadir tiempo a jugadores            |
| `PLimitTime.command.removetime`     | Quitar tiempo a jugadores            |
| `PLimitTime.command.resettime`      | Reiniciar tiempo de jugadores        |
| `PLimitTime.command.pausar`         | Pausar el tiempo de un jugador       |
| `PLimitTime.command.reanudar`       | Reanudar el tiempo de un jugador     |
| `PLimitTime.command.reload`         | Recargar configuración YAML          |

> ✅ Si **LuckPerms** no está disponible, se utilizará `hasPermissionLevel(4)` (operador) como mecanismo de fallback.

---

## ⚙️ Configuración YAML

Ruta: `config/playertimelimit.yaml`

```yaml
tiempoPorDefecto: 18000

reinicio:
  hora: "00:00"
  zonaHoraria: "America/Mexico_City"

advertencias:
  - tiempo: 1800
    mensaje: "⏳ Quedan 30 minutos de juego."
  - tiempo: 900
    mensaje: "⏳ Quedan 15 minutos de juego."
  - tiempo: 120
    mensaje: "⏳ Quedan 2 minutos. ¡Prepárate!"

mensajes:
  tiempoAgotado: " Tu tiempo de juego para hoy ha terminado. ¡Nos vemos mañana!"
  bienvenida: " ¡Bienvenido! Tienes %tiempo% segundos restantes de juego."
  tiempoAgregado: " ¡%tiempo% segundos han sido añadidos a tu sesión!"
  tiempoRemovido: " %tiempo% segundos fueron removidos de tu sesión."
  tiempoRestablecido: " Tu tiempo ha sido restablecido a %tiempo% segundos."
  pausado: "✨ Tu tiempo está actualmente pausado."

bossbar:
  message: "Tiempo restante: %horas%h %minutos%m %segundos%s"
  color: WHITE
```
---

## 🧩 API Pública

El mod expone una API pública para integraciones con otros mods. Puedes acceder a ella desde:

```java
import com.TNTStudios.playertimelimit.Playertimelimit;
import com.TNTStudios.playertimelimit.api.PlayerTimeLimitAPI;

PlayerTimeLimitAPI api = Playertimelimit.getAPI();

// Ejemplos de uso
api.addTime(uuid, 300);      // Añadir 5 minutos
api.removeTime(uuid, 60);    // Restar 1 minuto
api.pause(uuid);             // Pausar tiempo
api.resume(uuid);            // Reanudar tiempo
int tiempo = api.getTime(uuid); // Consultar tiempo restante
boolean pausado = api.isPaused(uuid); // Verificar si está pausado
```
---

## 🧩 Requisitos

- [Fabric API](https://modrinth.com/mod/fabric-api)
- Java 17+
- (Opcional) [LuckPerms](https://luckperms.net) para gestión avanzada de permisos.

---

## 📄 Licencia

**PlayerTimeLimit** es un proyecto con licencia `All Rights Reserved`.  
Su uso está permitido para servidores privados, educativos y personales.  
No se permite redistribuir ni modificar públicamente sin autorización previa.

---

## 🙌 Créditos

Desarrollado por **TNTStudios**.  
Diseñado con el propósito de fomentar un juego responsable, controlado y personalizable en comunidades de Minecraft.

¡Gracias por usar **PlayerTimeLimit**! 🎮


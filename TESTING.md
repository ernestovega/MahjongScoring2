# Tests

Todos los tests corren en la JVM (`app/src/test`), sin emulador: lógica pura con JUnit 5 y todo lo que toca Android
(Room, DataStore, ViewModels, pantallas) con Robolectric. Así son rápidos, reproducibles y corren igual en CI.

## Comandos

| Qué | Comando |
| --- | --- |
| Todo (tests + comparar screenshots) | `./gradlew :app:verifyRoborazziDebug` |
| Solo tests (sin comparar screenshots) | `./gradlew :app:testDebugUnitTest` |
| Regrabar screenshots tras un cambio visual intencionado | `./gradlew :app:recordRoborazziDebug` |
| Ver diffs de screenshots (`*_compare.png`) | `./gradlew :app:compareRoborazziDebug` |
| Regenerar goldens de texto tras un cambio lógico intencionado | `./gradlew :app:testDebugUnitTest -Pupdate.goldens=true` |

Revisa siempre el diff de git de los goldens regenerados antes de hacer commit: ese diff ES el cambio de comportamiento.

Los screenshots se grabaron en Linux. En macOS la renderización de fuentes puede variar algún píxel, así que la
verificación de referencia es la de CI (`.github/workflows/tests.yml`, ubuntu).

## Capas

| Capa | Dónde | Qué fija |
| --- | --- | --- |
| Lógica de negocio | `business/**` | `UiGame`, `UiRound`, enums, utilidades, `NeededPoints`/`TableDiffs`, todos los use cases (con repos fake) |
| Goldens de partidas | `golden/GameScenariosGoldenTest` | Para cada partida de `testutil/GameScenarios`: puntuación ronda a ronda, ranking, mejor mano, asientos, diffs, export CSV/texto/JSON y re-import |
| Datos | `data_source/**` | Room (ids, orden, FKs, converters), migración 1→2 en JVM, repos de DataStore, combinaciones en 6 idiomas |
| ViewModels | `app/viewmodels/**` | Estados que emiten y efectos en los repos |
| UI flujos | `ui/UiFlowsTest` | La app real (Hilt + Room en memoria) manejada como un usuario: crear partida, hu, autoservido, empate, penalizaciones, borrar ronda, terminar/reanudar, 16 rondas, editar nombres, menús |
| UI casos límite | `ui/UiEdgeCasesTest` | Teclado numérico (borrar, ceros, máximo 4 cifras), las 12 combinaciones ganador/descartador, penalizaciones acumuladas, cancelar diálogos, dados, compartir/exportar/importar (los intents que se envían), enlaces del menú lateral, cambio de idioma, botón atrás, menús visibles, recreación de la activity |
| UI textos | `ui/ScreenTextGoldenTest` | Golden de texto (`golden/screens/*.txt`) con todo lo que se lee en pantalla para cada partida: mesa en las 3 disposiciones, cada ronda de la lista, diálogo de acciones de cada asiento con su tabla de puntos necesarios, ranking, mesa tras 0..15 manos (posición del dado), listas de partidas, combinaciones y calculadora |
| Volumen | `golden/BulkRandomGamesTest` | 400 partidas aleatorias con semilla en golden (`golden/bulk/`), invariantes sobre 3000 partidas e ida y vuelta JSON de 300 |
| UI visual | `ui/ScreenshotsTest` | Screenshots golden: todas las pantallas y diálogos, disposiciones de asientos, la mesa tras cada mano, fuente grande del sistema, y pantallas y diálogos en los 6 idiomas |
| Regresiones | `ui/DialogLifecycleTest`, navegación en `ui/UiFlowsTest` | Bugs ya arreglados (diálogos al destruir la activity, volver a Old games desde la barra inferior) |

## Migración a KMP

Lo que está pensado para reutilizarse tal cual:

- `app/src/test/resources/golden/scenarios/<partida>/game.json`: la partida en el formato de export de la app.
- `.../report.txt`: el resultado esperado (puntos, ranking, mejor mano, asientos, diffs) en texto plano.
- `.../export.csv`, `export.txt`, `all_games_export.json`, `needed_points.csv`, `table_diffs.txt`, `combinations/*.txt`.

La versión KMP es una copia idéntica si, importando cada `game.json`, genera exactamente esos ficheros. Basta con
portar `testutil/GameReport.kt` (solo depende de los modelos) y comparar contra los mismos ficheros. Para la parte
visual, los mismos 47 escenarios de `ScreenshotsTest` con los mismos datos dan las imágenes de referencia.

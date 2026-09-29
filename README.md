# Alcalde Digital — Primera entrega (Árboles)

Videojuego educativo sobre el uso responsable de las redes sociales. Ciudad Nova elige alcalde y en la
red social Civitas circulan noticias verdaderas, rumores, opiniones y ataques. De 2 a 4 jugadores
(Ciudadano, Periodista, Influencer y Candidato) deciden por turnos qué hacer con cada publicación:
**Verificar, Compartir, Reportar o Ignorar**.

## Ejecutar
Abrir el proyecto en NetBeans y pulsar Run (clase principal `core.MainGUI`).

## Estructura
- `arboldecision`: árbol N-ario de decisiones (`ArbolDecision`, `NodoDecision`, `TipoNodo`, `Consecuencia`).
- `arbolclasificacion`: árbol N-ario de clasificación (`ArbolClasi`, `Clasificacion`, `Publicacion`).
- `core`: lógica del juego (`GameEngine`, `EstadoCiudad`, `Partida`, `Jugador`, `CatalogoPublicaciones`) y pantallas.
- `ui`: componentes visuales reutilizables (botones, tarjetas, tablas, visor de árboles).

## Árbol de decisión
- **Problema que resuelve:** representa todas las decisiones posibles ante cada publicación y sus consecuencias.
  El juego desciende desde la publicación hasta un resultado según lo que elige el jugador.
- **Por qué un árbol:** cada decisión abre caminos distintos que no se cruzan (padre → opciones → resultado).
- **Variante:** árbol N-ario. Cada nodo guarda sus hijos en un `LinkedHashMap<opción, nodo>`.
  Tipos de nodo: `RAIZ`, `PUBLICACION`, `PREGUNTA` y `RESULTADO` (este último con su `Consecuencia`).
- **Inserción y eliminación:** `insertar(ruta, opción, nodo)` baja recursivamente siguiendo la ruta y agrega el hijo
  (si el árbol está vacío, el nodo es la raíz). `eliminar(ruta)` quita la rama completa.
- **Recorridos:** `preorden()`, `postorden()`, `recorrerDFS()` y `buscarPorTipo()`.
- **Métricas:** `peso`, `hojas`, `altura` y `grado`.

## Árbol de clasificación
- **Problema que resuelve:** organiza las publicaciones por veracidad y categoría. Al pulsar **Verificar**,
  el motor llama a `verificar(id)` y `buscarPorId(id)` y muestra al jugador en qué rama está la publicación.
- **Variante:** árbol N-ario de categorías (`Todas → Verdadera/Falsa → Categoría`); cada nodo guarda sus publicaciones.
- **Inserción y eliminación:** `insertarPublicacion` crea las ramas que falten; `eliminarPublicacion(id)` la busca y la quita.
- **Recorridos:** `preorden()`, `postorden()`, `recorrerPreorden()`, `buscarPorCategoria()`.

## Relación con el juego
- Cada resultado del árbol de decisión modifica los puntos, la reputación y los indicadores de la ciudad
  (información verificada, confianza, convivencia, bienestar digital, desinformación y conflictos).
- Cada rol tiene una habilidad que cambia cómo afectan sus decisiones a la ciudad.
- En cada partida se eligen al azar 8 de 10 publicaciones y algunas se vuelven virales (menos tiempo, impacto ×2).
- Al final se calcula el puntaje de la ciudad y el resultado de la elección del alcalde.

## Visualizador de árboles
Muestra los dos árboles y el recorrido de la partida en vertical: cada fila es un nivel (empezando en 0).
Rueda: zoom · Shift + rueda o arrastrar: mover · Doble clic: enfocar un nodo. También tiene vista de subárbol.
Permite ejecutar los recorridos (numerando cada nodo en el orden de visita), buscar por categoría y consultar
la justificación de cada estructura.

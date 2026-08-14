# Conway's Game of Life - Live Wallpaper 🦠

Un fondo de pantalla animado (Live Wallpaper) para Android hiper-optimizado, construido con una arquitectura híbrida de C++ y Kotlin.

## 🏗️ Arquitectura del Proyecto

El proyecto está dividido en dos capas conectadas mediante JNI (Java Native Interface):

1. **Módulo C++ (Backend/Engine):**
    * Motor optimizado que utiliza técnicas de *Chunking* y *Bounding Box* para máxima eficiencia.
    * La cuadrícula toroidal bidimensional se mapea en memoria como un arreglo 1D contiguo (`std::vector<uint8_t>`).
2. **Capa Puente (JNI):**
    * Archivo `native-lib.cpp` expuesto mediante `extern "C"`.
    * Traduce los datos nativos copiando el vector C++ a un `jbyteArray` de manera rápida para la JVM.
3. **Módulo Kotlin (Frontend y UI):**
    * `ConwayWallpaperService` implementa el ciclo de vida del fondo de pantalla.
    * Dibuja iterando sobre el array en un hilo secundario mediante `SurfaceHolder/Canvas` para no bloquear la UI del sistema.
    * **Optimización energética:** Pausa el motor y el renderizado cuando la pantalla no es visible.

## 🚀 Instalación

1. Ve a la pestaña de [Releases](enlace_a_tus_releases) de este repositorio.
2. Descarga el archivo `app-release.apk`.
3. Instálalo en tu dispositivo Android (asegúrate de permitir la instalación desde orígenes desconocidos).
4. Aplica el fondo desde el selector de Live Wallpapers de tu sistema.
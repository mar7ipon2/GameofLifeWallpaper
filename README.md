# 🦠 Conway's Game of Life - Live Wallpaper

[ 🇬🇧 English ](#-english) | [ 🇪🇸 Español ](#-español) | [ ᴄᴀ Català ](#català)

---

## 🇬🇧 English

A hyper-optimized Live Wallpaper for Android, built with a hybrid C++ and Kotlin architecture.

### 🏗️ Architecture
This project is divided into two main layers connected via JNI (Java Native Interface)[cite: 14]:

1. **C++ Engine (Backend):**
   * Optimized engine using *Chunking* and *Bounding Box* techniques[cite: 12].
   * The 2D toroidal grid is mapped to a contiguous 1D array (`std::vector<uint8_t>`) for maximum memory and battery efficiency[cite: 12, 14].
2. **JNI Layer:**
   * The bridge file (`native-lib.cpp`) translates native C++ data types to Kotlin (`jbyteArray`) by efficiently copying memory[cite: 12].
3. **Kotlin Bridge & UI (Frontend):**
   * `ConwayWallpaperService` manages the wallpaper lifecycle, drawing on a secondary thread (via `SurfaceHolder/Canvas`) to avoid blocking the system UI[cite: 12, 14].
   * **Energy Optimization:** Pauses `update()` and rendering calls when `onVisibilityChanged()` is false[cite: 12, 14].

### 🚀 How to Install
> **⚠️ Important Note for some devices (Xiaomi, Samsung, etc.):** Because this app is built as a background `WallpaperService` to save battery, it might not appear in your standard app drawer.

1. Go to the **[Releases](../../releases)** tab of this repository.
2. Download the latest `app-release.apk` file to your Android device.
3. Open the downloaded `.apk` file to install it. (Allow installation from "Unknown Sources" if prompted).
4. **To apply the wallpaper:**
   * **Option A:** Open the app if an icon is available; it will try to launch the system's live wallpaper picker directly.
   * **Option B (Recommended):** Download the official **[Wallpapers app by Google](https://play.google.com/store/apps/details?id=com.google.android.apps.wallpaper)** from the Play Store. Open it, scroll down to "Live Wallpapers", and select **Game of Life Wallpaper**.

---

## 🇪🇸 Español

Un fondo de pantalla animado hiper-optimizado para Android, construido con una arquitectura híbrida de C++ y Kotlin.

### 🏗️ Arquitectura
El proyecto está dividido en dos capas conectadas mediante JNI (Java Native Interface)[cite: 14]:

1. **Motor C++ (Backend):**
   * Motor optimizado con técnicas de *Chunking* y *Bounding Box*[cite: 12].
   * Matriz toroidal bidimensional mapeada a un arreglo 1D contiguo (`std::vector<uint8_t>`) para maximizar la eficiencia de recursos y batería[cite: 12, 14].
2. **Capa JNI:**
   * El archivo puente (`native-lib.cpp`) traduce los datos nativos copiando la memoria de forma eficiente a `jbyteArray` para la JVM[cite: 12].
3. **Frontend Kotlin:**
   * `ConwayWallpaperService` implementa el ciclo de vida del fondo, dibujando en un hilo secundario (`SurfaceHolder/Canvas`) para no bloquear la interfaz del sistema[cite: 12, 14].
   * **Optimización energética:** Pausa las llamadas a `update()` y el dibujado cuando `onVisibilityChanged()` pasa a false (pantalla apagada o cubierta)[cite: 12, 14].

### 🚀 Guía de Instalación
> **⚠️ Nota importante para algunos dispositivos (Xiaomi, Samsung, etc.):** Debido a que esta aplicación está construida como un servicio en segundo plano (`WallpaperService`) para ahorrar batería, es posible que no aparezca un icono en tu cajón de aplicaciones.

1. Ve a la pestaña de **[Releases](../../releases)** en este repositorio.
2. Descarga el archivo `app-release.apk` en tu dispositivo Android.
3. Abre el archivo `.apk` para instalarlo. (Concede permiso para instalar aplicaciones de "Orígenes desconocidos" si se solicita).
4. **Para aplicar el fondo:**
   * **Opción A:** Si aparece el icono de la app, ábrela. Intentará abrir el selector de fondos del sistema automáticamente.
   * **Opción B (Recomendada):** Descarga la aplicación oficial **[Fondos de pantalla de Google](https://play.google.com/store/apps/details?id=com.google.android.apps.wallpaper)** desde la Play Store. Ábrela, baja hasta la sección de "Fondos Animados" y selecciona **Game of Life Wallpaper**.

---

## Català

Un fons de pantalla animat hiper-optimitzat per a Android, construït amb una arquitectura híbrida de C++ i Kotlin.

### 🏗️ Arquitectura
El projecte està dividit en dues capes connectades mitjançant JNI (Java Native Interface)[cite: 14]:

1. **Motor C++ (Backend):**
   * Motor optimitzat amb tècniques de *Chunking* i *Bounding Box*[cite: 12].
   * Matriu toroïdal bidimensional mapejada a un arranjament 1D contigu (`std::vector<uint8_t>`) per maximitzar l'eficiència de recursos i bateria[cite: 12, 14].
2. **Capa JNI:**
   * El fitxer pont (`native-lib.cpp`) tradueix les dades natives copiant la memòria de forma eficient a `jbyteArray` per a la JVM[cite: 12].
3. **Frontend Kotlin:**
   * `ConwayWallpaperService` implementa el cicle de vida del fons, dibuixant en un fil secundari (`SurfaceHolder/Canvas`) per no bloquejar la interfície del sistema[cite: 12, 14].
   * **Optimització energètica:** Pausa les crides a `update()` i el dibuixat quan `onVisibilityChanged()` passa a false[cite: 12, 14].

### 🚀 Guia d'Instal·lació
> **⚠️ Nota important per a alguns dispositius (Xiaomi, Samsung, etc.):** Com que aquesta aplicació està construïda com un servei en segon pla (`WallpaperService`) per estalviar bateria, és possible que no aparegui una icona al teu calaix d'aplicacions.

1. Vés a la pestanya de **[Releases](../../releases)** d'aquest repositori.
2. Descarrega el fitxer `app-release.apk` al teu dispositiu Android.
3. Obre el fitxer `.apk` per instal·lar-lo. (Dóna permís per instal·lar aplicacions d'"Orígens desconeguts" si el sistema t'ho demana).
4. **Per aplicar el fons:**
   * **Opció A:** Si apareix la icona de l'app, obre-la. Intentarà obrir el selector de fons del sistema automàticament.
   * **Opció B (Recomanada):** Descarrega l'aplicació oficial **[Fons de pantalla de Google](https://play.google.com/store/apps/details?id=com.google.android.apps.wallpaper)** des de la Play Store. Obre-la, baixa fins a la secció de "Fons Animats" i selecciona **Game of Life Wallpaper**.
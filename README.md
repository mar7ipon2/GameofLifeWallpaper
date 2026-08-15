# 🦠 Conway's Game of Life - Live Wallpaper

[ 🇬🇧 English ](#-english) | [ 🇪🇸 Español ](#-español) | [ ᴄᴀ Català ](#català)

---

## 🇬🇧 English

An optimized Live Wallpaper for Android, built with a hybrid C++ and Kotlin architecture.

### 🏗️ Architecture
The project is divided into two layers connected via JNI (Java Native Interface):

1. **C++ Engine (Backend):**
   * Optimized engine using *Chunking* (processing by regions) and *Bounding Box* (defining boundaries based on active cell regions) techniques.
   * Two-dimensional toroidal grid mapped to a contiguous 1D array (`std::vector<uint8_t>`) to ensure resource and battery efficiency.
2. **JNI Layer:**
   * The bridge file (`native-lib.cpp`) translates native data by efficiently copying memory to a `jbyteArray` for the JVM.
3. **Kotlin Frontend:**
   * `ConwayWallpaperService` implements the wallpaper lifecycle, drawing on a secondary thread (`SurfaceHolder/Canvas`) to avoid blocking the system UI.
   * **Energy Optimization:** Pauses `update()` calls and rendering when `onVisibilityChanged()` turns false.

### Features
The wallpaper includes several user configuration options:
1. **Initial Configuration:**
   * *Grid size*: 4 options available, with each number representing a larger grid (smaller cells).
   * *Color palette*: 9 different options included.
   * *Initial automaton*: 7 initial configurations to choose from.
     >⚠️ Note: Configurations depend on the screen. With a small size (large cells), the pattern may not be fully displayed.
2. **Editor Access:**
   * Once the wallpaper is set, you can access the editor by double-clicking.
     >⚠️ Note: Please note that the lock screen does not allow this access, so this functionality is restricted to the home screen.
3. **Editor:**
   * *Play and Pause*: From the editor, this button can be used to stop the animation.
   * *Reset*: Allows you to reset the automaton chosen in the initial configuration.
   * *Modifications*: From the editor, you can tap the screen to activate or deactivate cells as you wish. It is recommended to pause the program to use this feature.
   * *Close*: Closes the editor, applying the changes to the wallpaper. If the state was paused, the animation will resume.

### 🚀 How to Install
> **⚠️ Important note for some devices (Xiaomi, Samsung, etc.):** Because this app is built as a background service (`WallpaperService`) to save battery, an icon may not appear in your app drawer.

1. Go to the **[Releases](../../releases)** tab of this repository.
2. Download the `app-release.apk` file to your Android device.
3. Open the `.apk` file to install it. (Grant permission to install apps from "Unknown Sources" if prompted by the system).
4. **To apply the wallpaper:**
   * **Option A:** If the app icon appears, open it. It will try to launch the system's wallpaper picker automatically.
   * **Option B (Recommended):** Download the official **[Wallpapers by Google](https://play.google.com/store/apps/details?id=com.google.android.apps.wallpaper)** app from the Play Store. Open it, scroll down to the "Live Wallpapers" section, and select **Game of Life Wallpaper**.

---

## 🇪🇸 Español

Un fondo de pantalla animado optimizado para Android, construido con una arquitectura híbrida de C++ y Kotlin.

### 🏗️ Arquitectura
El proyecto está dividido en dos capas conectadas mediante JNI (Java Native Interface):

1. **Motor C++ (Backend):**
   * Motor optimizado con técnicas de *Chunking* (procesamiento por regiones) y *Bounding Box* (definir límites según las regiones de células vivas).
   * Matriz toroidal bidimensional mapeada mediante un vector 1D contiguo (`std::vector<uint8_t>`) para asegurar la eficiencia de recursos y batería.
2. **Capa JNI:**
   * El archivo puente (`native-lib.cpp`) traduce los datos nativos copiando la memoria de forma eficiente a `jbyteArray` para la JVM.
3. **Frontend Kotlin:**
   * `ConwayWallpaperService` implementa el ciclo de vida del fondo, dibujando en un hilo secundario (`SurfaceHolder/Canvas`) para no bloquear la interfaz del sistema.
   * **Optimización energética:** Pausa las llamadas a `update()` y el dibujado cuando `onVisibilityChanged()` pasa a false.

### Características
El wallpaper cuenta con diferentes opciones de configuración para el usuario:
1. **Configuración inicial:**
   * *Tamaño de la cuadrícula*: Se dispone de 4 opciones, siendo cada número una cuadrícula más grande (células más pequeñas).
   * *Paleta de colores*: Se incluyen 9 opciones diferentes.
   * *Autómata inicial*: Se ofrecen 7 configuraciones iniciales a probar.
     >⚠️ Nota: Las configuraciones dependen de la pantalla. Con un tamaño pequeño (células grandes) el patrón puede no mostrarse correctamente.
2. **Acceso al editor:**
   * Una vez se ha establecido el fondo de pantalla, se puede acceder al editor haciendo doble clic.
     >⚠️ Nota: Es importante tener en cuenta que la pantalla de bloqueo no permite este acceso, por lo que esta funcionalidad queda restringida a la pantalla de inicio.
3. **Editor:**
   * *Play y Pause*: Desde el editor se puede utilizar este botón para detener la animación.
   * *Reset*: Permite restablecer el autómata que se ha escogido en la configuración inicial.
   * *Modificaciones*: Desde el editor se puede pulsar la pantalla para activar o desactivar células al gusto. Para esta función es recomendable poner el programa en pausa.
   * *Close*: Cierra el editor trasladando los cambios al fondo de pantalla. Si el estado estaba en pausa, la animación se activará.

### 🚀 Guía de Instalación
> **⚠️ Nota importante para algunos dispositivos (Xiaomi, Samsung, etc.):** Debido a que esta aplicación está construida como un servicio en segundo plano (`WallpaperService`) para ahorrar batería, es posible que no aparezca un icono en tu cajón de aplicaciones.

1. Ve a la pestaña de **[Releases](../../releases)** de este repositorio.
2. Descarga el archivo `app-release.apk` en tu dispositivo Android.
3. Abre el archivo `.apk` para instalarlo. (Concede permiso para instalar aplicaciones de "Orígenes desconocidos" si el sistema te lo pide).
4. **Para aplicar el fondo:**
   * **Opción A:** Si aparece el icono de la app, ábrela. Intentará abrir el selector de fondos del sistema automáticamente.
   * **Opción B (Recomendada):** Descarga la aplicación oficial **[Fondos de pantalla de Google](https://play.google.com/store/apps/details?id=com.google.android.apps.wallpaper)** desde la Play Store. Ábrela, baja hasta la sección de "Fondos Animados" y selecciona **Game of Life Wallpaper**.

---

## Català

Un fons de pantalla animat optimitzat per a Android, construït amb una arquitectura híbrida de C++ i Kotlin.

### 🏗️ Arquitectura
El projecte està dividit en dues capes connectades mitjançant JNI (Java Native Interface):

1. **Motor C++ (Backend):**
   * Motor optimitzat amb tècniques de *Chunking* (processament per regions) i *Bounding Box* (definir fites segons les reginos de cèl·lules vives).
   * Matriu toroïdal bidimensional mapejada mitjançant un vector 1D contigu (`std::vector<uint8_t>`) per assegurar l'eficiència de recursos i bateria.
2. **Capa JNI:**
   * El fitxer pont (`native-lib.cpp`) tradueix les dades natives copiant la memòria de forma eficient a `jbyteArray` per a la JVM.
3. **Frontend Kotlin:**
   * `ConwayWallpaperService` implementa el cicle de vida del fons, dibuixant en un fil secundari (`SurfaceHolder/Canvas`) per no bloquejar la interfície del sistema.
   * **Optimització energètica:** Pausa les crides a `update()` i el dibuixat quan `onVisibilityChanged()` passa a false.

### Característiques
El wallpaper compta amb diferents opcions de configuració per l'usuari:
1. **Configuració inicial:**
   * *Mesura de la graella*: Es disposa de 4 opcions, éssent cada nombre una graella més gran (cèl·lules més petites).
   * *Paleta de colors*: S'inclouen 9 opcions diferents.
   * *Autòmata inicial*: S'ofereixen 7 configuracions inicials a provar.
     >⚠️ Nota: Les configuracions depenen de la pantalla. Amb una mesura petita (cèl·lules grans) la configuració pot no mostrar-se.
2. **Accés a l'editor:**
   * Un cop s'ha establert el fons de pantalla, es pot accedir a l'editor fent doble click.
     >⚠️ Nota: És important tenir en compte que la pantalla de bloqueig no permet aquest accés, de manera que aquesta funcionalitat queda restringida a la pantalla d'inici.
3. **Editor:**
   * *Play i Pause*: Des de l'editor es pot utilitzar aquest botó per parar l'animació.
   * *Reset*: Permet reestablir l'autòmata que s'ha escollit a la configuració inicial.
   * *Modificacions*: Des de l'editor es pot prèmer a la pantalla per activar o desactivar cèl·lules al gust. Per aquesta funció és recomanable posar el programa en pausa.
   * *Close*: Tanca l'editor traslladant els canvis al fons de pantalla. Si l'estat estava en pausa l'animació s'activarà.


### 🚀 Guia d'Instal·lació
> **⚠️ Nota important per a alguns dispositius (Xiaomi, Samsung, etc.):** Com que aquesta aplicació està construïda com un servei en segon pla (`WallpaperService`) per estalviar bateria, és possible que no aparegui una icona al teu calaix d'aplicacions.

1. Vés a la pestanya de **[Releases](../../releases)** d'aquest repositori.
2. Descarrega el fitxer `app-release.apk` al teu dispositiu Android.
3. Obre el fitxer `.apk` per instal·lar-lo. (Dóna permís per instal·lar aplicacions d'"Orígens desconeguts" si el sistema t'ho demana).
4. **Per aplicar el fons:**
   * **Opció A:** Si apareix la icona de l'app, obre-la. Intentarà obrir el selector de fons del sistema automàticament.
   * **Opció B (Recomanada):** Descarrega l'aplicació oficial **[Fons de pantalla de Google](https://play.google.com/store/apps/details?id=com.google.android.apps.wallpaper)** des de la Play Store. Obre-la, baixa fins a la secció de "Fons Animats" i selecciona **Game of Life Wallpaper**.
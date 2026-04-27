# Let's Kody - Aplicación móvil educativa

Aplicación móvil educativa desarrollada en **Android Studio** que combina aprendizaje y entretenimiento mediante juegos interactivos, gestión de usuarios y sistema de ranking.

## 📌 Descripción

**Let's Kody** es una aplicación educativa pensada para mejorar el aprendizaje en distintas materias como matemáticas, geografía y lengua, a través de mecánicas de juego.

Incluye:

- Sistema de detección de conexión a Internet.
- Sistema de autenticación de usuarios (email con contraseña o acceso con Google).
- Juegos educativos con diferente sistema de puntuación.
- Ranking de usuarios.
- Sección de entretenimiento.
- Gestión de perfil del usuario.
- Integración con Firebase y base de datos local.

## 🛠️ Tecnologías utilizadas

<p align="center"> 
    <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" /> 
    &nbsp;&nbsp;
    <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" /> 
    &nbsp;&nbsp; 
    <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" /> 
    &nbsp;&nbsp; 
    <img src="https://img.shields.io/badge/Room-3DDC84?style=for-the-badge&logo=android&logoColor=white" /> 
    &nbsp;&nbsp; 
    <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white" /> 
</p>

## 🎯 Objetivos

Los principales objetivos de este proyecto son:

- Fomentar el aprendizaje mediante gamificación.
- Practicar el desarrollo de aplicaciones Android modernas.
- Implementar sistemas reales de:
    - Autenticación.
    - Base de datos.
    - Gestión de usuarios.
    - Interfaz de Usuario dinámica.

## 🧩 Funcionalidades principales

### 🔐 Autenticación

- Registro de usuarios con email y verificación.
- Inicio de sesión con:
    - Email y contraseña.
    - Google.
- Gestión segura de cuentas.
- Recuperación y cambio de contraseña (solo usuarios normales).

### 🏠 Inicio

- Pantalla principal dinámica:
    - Ilustración animada y mensaje de bienvenida que cambia según el momento del día.
- Sistema de soporte técnico:
    - Envío de mensajes.
    - Cooldown de 5 minutos entre envíos.

### 📚 Materias

#### 🎮 Juegos educativos

Los juegos se organizan por materias:

- Matemáticas
- Geografía
- Lengua

Los **tipos de juegos** que hay disponibles son:

1. **Cronometrados**

    - 12 preguntas por partida.
    - Sistema de puntuación:
        - Aciertos → se suman puntos.
        - Fallos → se restan puntos + penalización de tiempo.
    - Cronómetro activo.

2. **Contrarreloj**

    - Tiempo limitado (2 minutos).
    - Sistema de puntuación:
        - Aciertos → se suman puntos.
        - Fallos → se registran.
    - El objetivo es conseguir la máxima puntuación posible.

**Al finalizar una partida** se le ofrece al usuario:

- Repetir el juego (sin guardar puntuación).
- Guardar la puntuación y salir.

El **sistema de puntuaciones** en los juegos se:

- Agrega la nueva puntuación si antes no existía ninguna.
- Actualiza si la nueva puntuación es mejor.

#### 🏆 Ranking

- Se obtiene la puntuación del usuario y su posición en el ranking global.
- Indicador visual según la posición del usuario.
    - Primero (dorado), segundo (plateado) y tercero (cobrizo).
    - El resto de posiciones (azul).

#### 🎲 Juegos de miscelánea

Juegos diseñados para el entretenimiento del usuario:

- Interacción con Kody (rebotar y estirar).
- Piano interactivo.
- Lienzo para dibujar.

### 👤 Perfil

- Visualización de los datos del usuario:
    - Foto de perfil.
    - Email.
    - Teléfono.
    - Edad.
- Edición de perfil:
    - Obligatorio cambiar la contraseña para usuarios normales.
- Eliminación de cuenta:
    - Se requiere confirmación mediante contraseña para usuarios normales.
- Los usuarios de Google realizan todas las gestiones sin uso de contraseña.


## 🗄️ Almacenamiento de datos

- ☁️ **Firebase**:
    - Autenticación de usuarios.
    - Base de datos en la nube (Firestore).

- 📱 **Room Database**:
    - Caché local.
    - Mejora del rendimiento.

## 📁 Estructura del proyecto

```
.
└── app/
    │
    ├── src/
    │   │
    │   └── main/
    │       │
    │       ├── java/com/juandeherrera/letskody/
    │       │   │
    │       │   ├── clasesAuxiliares/  # Modelos de datos 
    │       │   │
    │       │   ├── firebase/          # Gestión de datos de las colecciones de Firebase
    │       │   │   
    │       │   ├── localdb/           # Gestión de la base de datos local                   
    │       │   │
    │       │   ├── metodosAuxiliares/
    │       │   │   │
    │       │   │   ├── componentes/   # Componentes de la UI
    │       │   │   │
    │       │   │   ├── interfaz/      # Estilos visuales para la UI
    │       │   │   │
    │       │   │   └── operaciones/   # Lógica funcional del sistema
    │       │   │
    │       │   ├── navigation/        # Navegación entre pantallas
    │       │   │
    │       │   ├── notification/      # Sistema de notificaciones
    │       │   │
    │       │   ├── screens/           # Pantallas de la aplicación
    │       │   │    
    │       │   ├── ui/                # Configuración visual global de la aplicación
    │       │   │    
    │       │   ├── viewModels/        # Gestión de los juegos de las materias
    │       │   │
    │       │   └── MainActivity.kt    # Punto de entrada principal de la aplicación  
    │       │
    │       ├── res/                   # Recursos de la aplicación
    │       │
    │       └── AndroidManifest.xml    # Configuración global de la aplicación Android
    │
    ├── google-services.json   # Configuración de Firebase
    │
    └── build.gradle.kts       # Opciones de compilación, dependencias y ajustes para la app
```

## 📄 Licencia

Este proyecto es de uso **exclusivamente educativo**, para cualquier otro uso, es necesario contactar con el autor.

## 👨‍💻 Autor

Proyecto desarrollado por **Daniel Requejo Expósito**.
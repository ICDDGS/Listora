
# 📱 Listora

## 🎯 Objetivo de la App

**Listora** es una aplicación móvil diseñada para facilitar la organización de compras. Permite crear listas, agregar ingredientes o productos con nombre, cantidad, unidad, precio y marcarlos como comprados. Además, ofrece funcionalidades para visualizar resúmenes de gasto, historial de compras y exportación a Excel, así como la opción de que el usuario guarde sus recetas de cocina.

## 🧩 Descripción del Logo

El logo de Listora está conformado por una lista, que representa la organización de compras, acompañada de una bolsa que simboliza el acto de adquirir productos. En conjunto, el diseño comunica de forma visual y directa la finalidad principal de la aplicación: gestionar compras de manera eficiente.

## 💡 Justificación técnica

- **Tipo de dispositivo**: La app está pensada para smartphones, ya que son dispositivos que siempre tenemos a la mano, permitiendo acceder a la aplicación cuando sea necesario.
- **Versión mínima del sistema operativo**: Android 7.0 (API 24) en adelante. Esta elección garantiza compatibilidad con una amplia gama de dispositivos actuales sin sacrificar funcionalidades modernas como notificaciones o almacenamiento local avanzado.
- **Orientaciones soportadas**: Solo se soporta **orientación vertical (portrait)**, con el objetivo de mantener una experiencia centrada, sencilla y cómoda para el usuario.
- **Notificaciones**: La app permite configurar notificaciones locales programadas por el usuario, que le recuerdan listas de compras no finalizadas. Esta funcionalidad mejora la planificación y el seguimiento de compras pendientes.

## 🔐 Credenciales de acceso

Actualmente, la app **no requiere credenciales de acceso**. Todas las funcionalidades están disponibles localmente sin necesidad de autenticación, lo que permite al usuario empezar a usar la app de inmediato tras la instalación.

## 🧱 Dependencias y frameworks utilizados

El proyecto está desarrollado en **Kotlin** con Android SDK. Algunas dependencias clave incluyen:

- **Room** – Para almacenamiento persistente de datos locales (listas, ingredientes, historial).
- **AndroidX WorkManager** (v2.10.2) – Para programación fiable de tareas en background, como envío de notificaciones
- **MPAndroidChart** – Para generación de gráficas y estadísticas visuales.
- **Apache POI o JExcelAPI (opcional)** – Para exportación de datos en formato Excel.
- **Jetpack Components** – Navigation, ViewModel, LiveData, etc.
- **Coroutines + KTX** – Para manejar operaciones asíncronas sin bloquear la UI.
- **UserNotifications / AlarmManager** – Para la gestión de notificaciones locales (programación y entrega).

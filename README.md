# 📱 Digital Files System — App de Asistencia

> Aplicación móvil Android para gestión de asistencia de empleados, desarrollada en **Kotlin** con **Material Design 3** y conectada a una API REST en **Java Servlets (NetBeans + Tomcat)** con base de datos **MySQL**.

---

## 📋 Tabla de contenidos

- [Descripción](#descripción)
- [Tecnologías](#tecnologías)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Modificaciones realizadas](#modificaciones-realizadas)
- [Configuración de la API](#configuración-de-la-api)
- [Instalación y uso](#instalación-y-uso)
- [Pantallas](#pantallas)
- [Base de datos](#base-de-datos)
- [Credenciales de prueba](#credenciales-de-prueba)

---

## Descripción

Sistema de asistencia empresarial que permite a los empleados registrar su entrada y salida mediante una app Android. La app se conecta a un servidor Java (NetBeans/Tomcat) que gestiona la autenticación y los registros contra una base de datos MySQL (`asistencia_db`).

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| App móvil | Kotlin · Android Studio · Material Design 3 |
| Backend | Java Servlets · NetBeans · Apache Tomcat 8080 |
| Base de datos | MySQL · `asistencia_db` |
| Comunicación | HTTP REST · JSON |
| Autenticación | SHA2-256 (coincide con `SHA2('pass',256)` de MySQL) |

---

## Estructura del proyecto

```
app/src/main/
├── java/com/proyecto/app_de_asistencia/
│   ├── MainActivity.kt        ← Pantalla de login con llamada real a la API
│   ├── dashboard.kt           ← Pantalla principal post-login
│   ├── AppConfig.kt           ← ⚙️ CONFIGURA AQUÍ LA URL DE TU SERVIDOR
│   └── ApiClient.kt           ← Cliente HTTP (HttpURLConnection, sin librerías externas)
│
└── res/
    ├── layout/
    │   ├── activity_main.xml       ← Layout login (tema oscuro del mockup)
    │   └── activity_dashboard.xml  ← Layout dashboard con sidebar
    ├── drawable/
    │   ├── bg_card_dark.xml        ← Fondo de las cards (azul marino + borde)
    │   └── bg_sidebar_active.xml   ← Ícono activo del sidebar
    └── values/
        ├── colors.xml    ← Paleta completa Digital Files System
        ├── themes.xml    ← Tema oscuro Material 3
        └── strings.xml
```

---

## Modificaciones realizadas

### 1. `gradle/libs.versions.toml`
Se agregó Kotlin como dependencia del proyecto (faltaba completamente).

```toml
[versions]
agp = "9.0.1"
kotlin = "2.1.20"          # ← AGREGADO
coreKtx = "1.17.0"
junit = "4.13.2"
junitVersion = "1.3.0"
espressoCore = "3.7.0"
appcompat = "1.7.1"
material = "1.13.0"
activity = "1.12.3"
constraintlayout = "2.2.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-appcompat = { group = "androidx.appcompat", name = "appcompat", version.ref = "appcompat" }
material = { group = "com.google.android.material", name = "material", version.ref = "material" }
androidx-activity = { group = "androidx.activity", name = "activity", version.ref = "activity" }
androidx-constraintlayout = { group = "androidx.constraintlayout", name = "constraintlayout", version.ref = "constraintlayout" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }   # ← AGREGADO
```

---

### 2. `build.gradle.kts` (raíz del proyecto)
Se declaró el plugin Kotlin a nivel de proyecto con `apply false`.

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false   // ← AGREGADO
}
```

---

### 3. `app/build.gradle.kts`
Tres correcciones críticas:
- Se agregó el plugin `kotlin.android`
- Se corrigió la sintaxis de `compileSdk` (la versión original usaba `release()` que no existe)
- Se eliminó la dependencia duplicada de Material
- Se agregó `kotlinOptions { jvmTarget = "11" }`

```kotlin
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.proyecto.app_de_asistencia"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.proyecto.app_de_asistencia"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
```

---

### 4. `AndroidManifest.xml`
Se agregaron los permisos de red (sin esto la app no puede conectarse al servidor).

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <!-- PERMISOS DE RED — obligatorios para conectarse al servidor -->
    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:dataExtractionRules="@xml/data_extraction_rules"
        android:fullBackupContent="@xml/backup_rules"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.Appdeasistencia"
        android:usesCleartextTraffic="true">

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".dashboard"
            android:exported="false" />

    </application>
</manifest>
```

---

### 5. `res/values/colors.xml`
Se reemplazó el archivo vacío con la paleta completa del diseño.

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>

    <!-- Paleta Digital Files System -->
    <color name="dfs_bg">#0B1629</color>           <!-- Fondo principal -->
    <color name="dfs_surface">#0F1E30</color>       <!-- Cards -->
    <color name="dfs_surface2">#162338</color>      <!-- Cards secundarias -->
    <color name="dfs_header">#081020</color>        <!-- Header -->
    <color name="dfs_sidebar">#0A1828</color>       <!-- Sidebar -->
    <color name="dfs_accent">#1A6EF7</color>        <!-- Azul botones -->
    <color name="dfs_accent_light">#4D9FFF</color>  <!-- Links -->
    <color name="dfs_text">#E2EAF5</color>          <!-- Texto principal -->
    <color name="dfs_muted">#7A94B0</color>         <!-- Texto secundario -->
    <color name="dfs_border">#1A2E45</color>        <!-- Bordes -->
    <color name="dfs_logout">#1A2A3A</color>        <!-- Botón cerrar sesión -->
</resources>
```

---

### 6. `res/values/themes.xml` y `res/values-night/themes.xml`
Se configuró el tema oscuro de Material 3 con los colores del diseño en ambos archivos.

```xml
<resources>
    <style name="Base.Theme.Appdeasistencia"
        parent="Theme.Material3.Dark.NoActionBar">
        <item name="colorPrimary">#1A6EF7</item>
        <item name="colorOnPrimary">#FFFFFF</item>
        <item name="android:colorBackground">#0B1629</item>
        <item name="colorSurface">#0F1E30</item>
        <item name="colorOnSurface">#E2EAF5</item>
        <item name="colorOutline">#1A2E45</item>
    </style>
    <style name="Theme.Appdeasistencia" parent="Base.Theme.Appdeasistencia"/>
</resources>
```

---

### 7. `res/layout/activity_main.xml`
Se rediseñó completamente el layout del login para que coincida con el mockup (tema oscuro, header, card con bordes, footer).

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:background="@color/dfs_bg">

    <!-- HEADER -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="56dp"
        android:background="@color/dfs_header"
        android:orientation="horizontal"
        android:gravity="center_vertical"
        android:paddingStart="20dp"
        android:paddingEnd="20dp">
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="⬡  DIGITAL FILES SYSTEM"
            android:textColor="@color/dfs_text"
            android:textSize="13sp"
            android:textStyle="bold"
            android:letterSpacing="0.08"/>
    </LinearLayout>

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:fillViewport="true">
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:gravity="center"
            android:padding="28dp">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@drawable/bg_card_dark"
                android:orientation="vertical"
                android:padding="28dp">

                <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="INICIAR SESIÓN"
                    android:textColor="@color/dfs_text"
                    android:textSize="20sp"
                    android:textStyle="bold"
                    android:gravity="center"
                    android:letterSpacing="0.1"
                    android:layout_marginBottom="24dp"/>

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="ID de Empleado"
                    android:textColor="@color/dfs_muted"
                    android:textSize="12sp"
                    android:layout_marginBottom="6dp"/>

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/inputUsuario"
                    style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    app:hintEnabled="false"
                    android:layout_marginBottom="16dp">
                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/etUsuario"
                        android:layout_width="match_parent"
                        android:layout_height="52dp"
                        android:hint="EMP-00123"
                        android:textColor="@color/dfs_text"
                        android:textColorHint="@color/dfs_muted"
                        android:inputType="text"
                        android:imeOptions="actionNext"/>
                </com.google.android.material.textfield.TextInputLayout>

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="Contraseña"
                    android:textColor="@color/dfs_muted"
                    android:textSize="12sp"
                    android:layout_marginBottom="6dp"/>

                <com.google.android.material.textfield.TextInputLayout
                    android:id="@+id/inputPassword"
                    style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    app:hintEnabled="false"
                    app:endIconMode="password_toggle"
                    android:layout_marginBottom="24dp">
                    <com.google.android.material.textfield.TextInputEditText
                        android:id="@+id/etPassword"
                        android:layout_width="match_parent"
                        android:layout_height="52dp"
                        android:hint="••••••••"
                        android:textColor="@color/dfs_text"
                        android:textColorHint="@color/dfs_muted"
                        android:inputType="textPassword"
                        android:imeOptions="actionDone"/>
                </com.google.android.material.textfield.TextInputLayout>

                <com.google.android.material.button.MaterialButton
                    android:id="@+id/btnIngresar"
                    style="@style/Widget.Material3.Button"
                    android:layout_width="match_parent"
                    android:layout_height="52dp"
                    android:text="Iniciar Sesión"
                    android:textSize="15sp"
                    android:textStyle="bold"
                    app:backgroundTint="@color/dfs_accent"
                    app:cornerRadius="8dp"
                    android:layout_marginBottom="14dp"/>

                <com.google.android.material.progressindicator.LinearProgressIndicator
                    android:id="@+id/progressLogin"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:visibility="gone"
                    app:indicatorColor="@color/dfs_accent"
                    android:layout_marginBottom="8dp"/>

                <TextView
                    android:id="@+id/tvOlvide"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:text="¿Olvidaste tu contraseña?"
                    android:textColor="@color/dfs_accent_light"
                    android:textSize="13sp"
                    android:gravity="center"
                    android:clickable="true"
                    android:focusable="true"/>
            </LinearLayout>
        </LinearLayout>
    </ScrollView>

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="© 2024 Digital Files System"
        android:textColor="@color/dfs_muted"
        android:textSize="11sp"
        android:gravity="center"
        android:padding="14dp"/>

</LinearLayout>
```

---

### 8. `res/drawable/bg_card_dark.xml` ← NUEVO ARCHIVO
Fondo de las cards del login y dashboard.

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="@color/dfs_surface"/>
    <corners android:radius="12dp"/>
    <stroke android:width="1dp" android:color="@color/dfs_border"/>
</shape>
```

---

### 9. `MainActivity.kt`
Reescrito completamente con lógica de login real: validación local, llamada HTTP en hilo de fondo, manejo de respuesta JSON, navegación al dashboard y barra de progreso.

```kotlin
package com.proyecto.app_de_asistencia

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var etUsuario    : TextInputEditText
    private lateinit var etPassword   : TextInputEditText
    private lateinit var btnIngresar  : MaterialButton
    private lateinit var progressLogin: LinearProgressIndicator
    private lateinit var tvOlvide     : TextView

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUsuario     = findViewById(R.id.etUsuario)
        etPassword    = findViewById(R.id.etPassword)
        btnIngresar   = findViewById(R.id.btnIngresar)
        progressLogin = findViewById(R.id.progressLogin)
        tvOlvide      = findViewById(R.id.tvOlvide)

        btnIngresar.setOnClickListener { intentarLogin() }
        tvOlvide.setOnClickListener {
            Toast.makeText(this, "Recuperación de contraseña próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun intentarLogin() {
        val usuario  = etUsuario.text?.toString()?.trim() ?: ""
        val password = etPassword.text?.toString() ?: ""

        if (usuario.isEmpty()) {
            etUsuario.error = "Ingresa tu ID de empleado"
            etUsuario.requestFocus(); return
        }
        if (password.isEmpty()) {
            etPassword.error = "Ingresa tu contraseña"
            etPassword.requestFocus(); return
        }

        setLoading(true)

        executor.execute {
            try {
                val body = JSONObject().apply {
                    put("usuario",    usuario)
                    put("contrasena", password)
                }
                val respuesta = ApiClient.postJson(AppConfig.URL_LOGIN, body)

                runOnUiThread {
                    setLoading(false)
                    if (respuesta.optBoolean("success", false)) {
                        navegarDashboard(respuesta)
                    } else {
                        val msg = respuesta.optString("mensaje", "Usuario o contraseña incorrectos")
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                        etPassword.error = msg
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    setLoading(false)
                    Toast.makeText(
                        this,
                        "Sin conexión. Verifica que el servidor esté activo.\n${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun navegarDashboard(resp: JSONObject) {
        val usuario = resp.optJSONObject("usuario") ?: return
        startActivity(
            Intent(this, dashboard::class.java).apply {
                putExtra("id_usuario",  usuario.optLong("id"))
                putExtra("nombre",      usuario.optString("nombre"))
                putExtra("apellido",    usuario.optString("apellido"))
                putExtra("rol",         usuario.optString("rol"))
                putExtra("id_empleado", usuario.optLong("idEmpleado"))
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
        )
    }

    private fun setLoading(loading: Boolean) {
        progressLogin.visibility = if (loading) View.VISIBLE else View.GONE
        btnIngresar.isEnabled    = !loading
        btnIngresar.text         = if (loading) "Verificando..." else "Iniciar Sesión"
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }
}
```

---

### 10. `AppConfig.kt` ← NUEVO ARCHIVO
Archivo centralizado con la URL del servidor. **Solo hay que cambiar `HOST` y `PROJECT`.**

```kotlin
package com.proyecto.app_de_asistencia

object AppConfig {
    // ============================================================
    //  CONFIGURA AQUÍ LA URL DE TU SERVIDOR
    //  Emulador Android  → HOST = "10.0.2.2"
    //  Celular físico    → HOST = IP de tu PC en WiFi (ej: "192.168.1.15")
    //  Ver IP de tu PC   → cmd → ipconfig → IPv4
    // ============================================================
    private const val HOST    = "10.0.2.2"
    private const val PORT    = "8080"
    private const val PROJECT = "AsistenciaAPI"   // nombre exacto de tu proyecto NetBeans

    val BASE_URL       = "http://$HOST:$PORT/$PROJECT"
    val URL_LOGIN      = "$BASE_URL/api/login"
    val URL_ASISTENCIA = "$BASE_URL/api/asistencia"
    const val TIMEOUT_MS = 10_000
}
```

---

### 11. `ApiClient.kt` ← NUEVO ARCHIVO
Cliente HTTP que usa `HttpURLConnection` — sin librerías externas.

```kotlin
package com.proyecto.app_de_asistencia

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

object ApiClient {

    fun postJson(urlString: String, body: JSONObject): JSONObject {
        var conn: HttpURLConnection? = null
        try {
            conn = (URL(urlString).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                setRequestProperty("Accept", "application/json")
                doOutput = true
                connectTimeout = AppConfig.TIMEOUT_MS
                readTimeout    = AppConfig.TIMEOUT_MS
            }
            conn.outputStream.use { os ->
                os.write(body.toString().toByteArray(StandardCharsets.UTF_8))
            }
            val stream = if (conn.responseCode >= 400) conn.errorStream else conn.inputStream
            val text   = BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8))
                .use { it.readText() }
            return JSONObject(text)
        } finally {
            conn?.disconnect()
        }
    }
}
```

---

## Configuración de la API

### Paso 1 — Abre `AppConfig.kt` y edita estas 3 constantes

```kotlin
private const val HOST    = "10.0.2.2"       // ← cambia según dónde corres la app
private const val PORT    = "8080"            // ← puerto de Tomcat (por defecto 8080)
private const val PROJECT = "AsistenciaAPI"   // ← nombre exacto de tu proyecto en NetBeans
```

### Paso 2 — Elige el valor de HOST según tu caso

| Dónde corres la app | Valor de HOST |
|---|---|
| Emulador de Android Studio | `"10.0.2.2"` |
| Celular físico (misma WiFi) | IP de tu PC, ej: `"192.168.1.15"` |

> **¿Cómo saber tu IP?** → Abre `cmd` en Windows → escribe `ipconfig` → busca **Dirección IPv4** bajo tu adaptador WiFi.

### Paso 3 — Verifica que el servidor esté corriendo

Antes de probar la app, asegúrate de que en NetBeans:
1. El proyecto `AsistenciaAPI` esté desplegado en Tomcat
2. Tomcat esté corriendo en el puerto `8080`
3. Puedas acceder desde el navegador a `http://localhost:8080/AsistenciaAPI/api/login`

---

## Instalación y uso

### Requisitos
- Android Studio Hedgehog o superior
- JDK 11
- Android SDK 36
- Dispositivo/emulador con Android 10+ (API 29)

### Paso a paso

```bash
# 1. Clona el repositorio
git clone https://github.com/etawaaesperu/proyecto-app-de-asistencia.git

# 2. Abre el proyecto en Android Studio
# File → Open → selecciona la carpeta del proyecto

# 3. Espera que Gradle sincronice automáticamente
# Si no sincroniza: File → Sync Project with Gradle Files

# 4. Configura la URL del servidor en AppConfig.kt

# 5. Ejecuta la app
# Run → Run 'app'  (Shift + F10)
```

---

## Pantallas

| Pantalla | Archivo | Descripción |
|---|---|---|
| Login | `MainActivity.kt` + `activity_main.xml` | Autenticación con usuario y contraseña |
| Dashboard | `dashboard.kt` + `activity_dashboard.xml` | Menú principal con sidebar de navegación |

### Respuesta JSON esperada del servidor

**Login exitoso (HTTP 200):**
```json
{
  "success": true,
  "mensaje": "Login exitoso",
  "usuario": {
    "id": 1,
    "usuario": "admin",
    "nombre": "Admin",
    "apellido": "Sistema",
    "idEmpleado": 1,
    "rol": "administrador"
  }
}
```

**Login fallido (HTTP 401):**
```json
{
  "success": false,
  "mensaje": "Contraseña incorrecta. Intentos: 2/5"
}
```

**Cuenta bloqueada (HTTP 403):**
```json
{
  "success": false,
  "mensaje": "Cuenta bloqueada. Inténtalo más tarde."
}
```

---

## Base de datos

La app se conecta a la base de datos `asistencia_db` en MySQL con las siguientes tablas principales:

| Tabla | Descripción |
|---|---|
| `usuarios` | Credenciales de acceso (usuario, contrasena_hash SHA2-256) |
| `empleados` | Datos del empleado (nombre, apellido, departamento) |
| `roles` | Roles del sistema (administrador, supervisor, empleado) |
| `usuario_roles` | Relación muchos a muchos entre usuarios y roles |
| `horarios` | Turnos asignados por empleado |
| `asistencia` | Registros de entrada/salida con DATETIME |

---

## Credenciales de prueba

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | admin123 | Administrador |
| `supervisor` | sup123 | Supervisor |
| `empleado1` | emp123 | Empleado |

---

## Autor

**etawaaesperu** — Proyecto académico de aplicación móvil de asistencia empresarial.

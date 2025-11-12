# 🖨️ PSP-DAM-ACTEVA02 - Sistema de Impresión Multiproceso

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)

Sistema de gestión de impresión compartida mediante **procesos concurrentes** y **semáforos binarios** para garantizar la **exclusión mutua**. Implementado tanto en **Java** como en **Python**.

---

## 📋 Descripción

Este proyecto simula el acceso concurrente de múltiples usuarios a una única impresora compartida. El sistema implementa:

- ✅ **Exclusión mutua** mediante semáforos binarios
- ✅ **Procesos independientes** (no hilos)
- ✅ **Sincronización** entre procesos
- ✅ **Sección crítica** protegida
- ✅ **Comunicación entre procesos**

### 🎯 Objetivo Académico

Demostrar la comprensión de los conceptos de programación concurrente:
- Procesos vs Hilos
- Semáforos y exclusión mutua
- Secciones críticas
- Sincronización de procesos
- Problemas de concurrencia (condiciones de carrera)

---

## 🏗️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────┐
│         GESTOR DE IMPRESIÓN (Principal)         │
│    - Coordina todos los procesos                │
│    - Inicializa el semáforo binario             │
│    - Lanza usuarios y impresora                 │
└─────────────┬───────────────────────────────────┘
              │
              ├──────────┬──────────┬──────────┐
              ▼          ▼          ▼          ▼
         ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌──────────┐
         │Usuario-1│ │Usuario-2│ │Usuario-3│ │Impresora │
         │(Proceso)│ │(Proceso)│ │(Proceso)│ │(Proceso) │
         └────┬────┘ └────┬────┘ └────┬────┘ └─────┬────┘
              │           │           │            │
              └───────────┴───────────┴────────────┘
                          │
                    ┌─────▼─────┐
                    │ SEMÁFORO  │
                    │  Binario  │
                    │ (0 o 1)   │
                    └───────────┘
```

---

## 📁 Estructura del Proyecto

```
PSP-DAM-ACTEVA02/
├── 📂 Java/
│   ├── javaVersion/
│   │   ├── GestorImpresion.java    # Coordinador principal
│   │   ├── Impresora.java          # Proceso impresora
│   │   └── Usuario.java            # Proceso usuario
│   └── README_JAVA.md
│
├── 📂 Python/
│   ├── Main.py                     # Punto de entrada
│   ├── GestorImpresion.py          # Coordinador
│   ├── Usuario.py                  # Clase Usuario
│   ├── Impresora.py                # Clase Impresora
│   └── README_PYTHON.md
│
├── 📂 Video/
│   └── explicacion.mp4
│
├── PSP-DAM-ActEva02_EstebanGarces.html  # Presentación web
└── README.md                            # Este archivo
```

---

## 🚀 Implementaciones

### ☕ Implementación en Java

#### Características Técnicas

- **Procesos reales**: Usa `ProcessBuilder` para crear procesos independientes del SO
- **Semáforo**: Implementado mediante archivos `.lock` (createNewFile())
- **Comunicación**: Cola de trabajos en archivo de texto
- **Sincronización**: Espera activa (polling) con verificación de archivo

#### Clases Principales

| Clase | Responsabilidad |
|-------|----------------|
| `GestorImpresion` | Coordina el sistema, crea procesos de usuarios e impresora |
| `Usuario` | Proceso que envía trabajos a la cola |
| `Impresora` | Proceso que consume trabajos de la cola |

#### Ejecución

```bash
# Compilar
cd Java/javaVersion
javac *.java

# Ejecutar
java javaVersion.GestorImpresion
```

#### Algoritmo del Semáforo (Java)

```java
// Adquirir semáforo (acquire)
while (!semaforoFile.createNewFile()) {
    Thread.sleep(100);  // Espera activa
}

// SECCIÓN CRÍTICA
// ... código que accede al recurso compartido ...

// Liberar semáforo (release)
semaforoFile.delete();
```

---

### 🐍 Implementación en Python

#### Características Técnicas

- **Procesos reales**: Usa `multiprocessing.Process`
- **Semáforo**: Objeto `multiprocessing.Semaphore(1)` nativo
- **Comunicación**: Semáforo compartido en memoria
- **Sincronización**: Bloqueo gestionado por el sistema operativo

#### Clases Principales

| Clase/Módulo | Responsabilidad |
|-------------|----------------|
| `Main` | Punto de entrada del programa |
| `GestorImpresion` | Coordina procesos y gestiona el semáforo |
| `Usuario` | Clase que representa un usuario |
| `Impresora` | Clase que representa la impresora |

#### Ejecución

```bash
# Ejecución estándar (3 usuarios, 2 trabajos cada uno)
cd Python
python Main.py

# Ejecución con parámetros personalizados
python Main.py 5 3  # 5 usuarios, 3 trabajos cada uno
```

#### Algoritmo del Semáforo (Python)

```python
# Adquirir semáforo
semaforo.acquire()  # Bloquea si está ocupado

try:
    # SECCIÓN CRÍTICA
    # ... código que accede al recurso compartido ...
finally:
    # Liberar semáforo
    semaforo.release()
```

---

## 🔄 Flujo de Ejecución

1. **Inicialización**
   - El `GestorImpresion` crea el semáforo binario (valor = 1)
   - Se lanza el proceso de la `Impresora`

2. **Creación de Usuarios**
   - Se crean N procesos de `Usuario` (por defecto N=3)
   - Cada usuario tiene M trabajos pendientes (por defecto M=2)

3. **Competencia por la Impresora**
   - Los usuarios intentan adquirir el semáforo
   - Solo uno puede entrar a la sección crítica
   - Los demás esperan bloqueados

4. **Impresión de Trabajos**
   - El usuario imprime su trabajo (simula 2 segundos)
   - Libera el semáforo al terminar
   - Otro usuario puede adquirirlo

5. **Finalización**
   - Todos los procesos terminan cuando completan sus trabajos
   - El gestor espera a que finalicen todos

---

## 📊 Ejemplo de Salida

```
==================================================
SISTEMA DE IMPRESIÓN MULTIPROCESO
==================================================
[Impresora] Impresora lista para recibir trabajos

Iniciando 3 usuarios...
Cada usuario enviará 2 trabajos

[Usuario-1] Solicita la impresora...
[Usuario-1] Imprimiendo trabajo 1
[Usuario-2] Solicita la impresora...
[Usuario-3] Solicita la impresora...
[Usuario-1] Termina trabajo 1
[Usuario-2] Imprimiendo trabajo 1
[Usuario-2] Termina trabajo 1
[Usuario-3] Imprimiendo trabajo 1
[Usuario-3] Termina trabajo 1
[Usuario-1] Solicita la impresora...
[Usuario-1] Imprimiendo trabajo 2
[Usuario-1] Termina trabajo 2
[Usuario-2] Solicita la impresora...
[Usuario-2] Imprimiendo trabajo 2
[Usuario-2] Termina trabajo 2
[Usuario-3] Solicita la impresora...
[Usuario-3] Imprimiendo trabajo 2
[Usuario-3] Termina trabajo 2

==================================================
Todos los trabajos han sido impresos
==================================================
```

---

## 🔑 Conceptos Clave

### Exclusión Mutua
Solo un proceso puede estar en la **sección crítica** a la vez. Esto evita:
- Condiciones de carrera
- Inconsistencia de datos
- Corrupción del recurso compartido

### Semáforo Binario
Variable compartida con solo dos valores posibles:
- **0**: Recurso ocupado (semáforo adquirido)
- **1**: Recurso disponible (semáforo libre)

### Sección Crítica
Fragmento de código que accede al recurso compartido y debe ejecutarse de forma atómica.

```
┌─────────────────────────┐
│   Región No Crítica     │
└────────────┬────────────┘
             │
┌────────────▼────────────┐
│ semaforo.acquire()      │  ◄── Entrada a sección crítica
├─────────────────────────┤
│   SECCIÓN CRÍTICA       │  ◄── Solo un proceso a la vez
│   (Usar impresora)      │
├─────────────────────────┤
│ semaforo.release()      │  ◄── Salida de sección crítica
└────────────┬────────────┘
             │
┌────────────▼────────────┐
│   Región No Crítica     │
└─────────────────────────┘
```

---

## 📈 Comparación Java vs Python

| Aspecto | Java | Python |
|---------|------|--------|
| **Procesos** | `ProcessBuilder` | `multiprocessing.Process` |
| **Semáforo** | Archivos `.lock` (manual) | `Semaphore(1)` (nativo) |
| **Sincronización** | Espera activa (polling) | Bloqueo del SO |
| **Comunicación** | Archivos de texto | Memoria compartida |
| **Complejidad** | Media-Alta | Baja-Media |
| **Portabilidad** | Alta | Alta |
| **Rendimiento** | Bueno | Muy bueno |

---

## 🎥 Video Explicativo

📹 [Ver video en YouTube](https://www.youtube.com/watch?v=ynFwIGYH-hI)

El video incluye:
- Explicación teórica de semáforos y exclusión mutua
- Demostración de ambas implementaciones
- Análisis del flujo de ejecución
- Comparación entre Java y Python
- Casos de prueba y resultados

---

## 🛠️ Requisitos

### Para Java
- **JDK**: Java 11 o superior
- **IDE**: IntelliJ IDEA, Eclipse o VSCode con Extension Pack for Java

### Para Python
- **Python**: 3.6 o superior
- **Módulos**: `multiprocessing` (incluido en la biblioteca estándar)

---

## 📚 Conceptos Teóricos Aplicados

### 1. Procesos vs Hilos

| Característica | Proceso | Hilo |
|---------------|---------|------|
| Memoria | Espacio propio | Comparten memoria |
| Creación | Más costosa | Más ligera |
| Comunicación | IPC necesaria | Directa (variables compartidas) |
| Aislamiento | Alto | Bajo |

**Este proyecto usa PROCESOS** para demostrar sincronización real entre entidades independientes del sistema operativo.

### 2. Problemas de Concurrencia

#### Sin Semáforo (❌ Incorrecto)
```
Usuario-1: Leer cola → [Trabajo A]
Usuario-2: Leer cola → [Trabajo A]  ← Ambos leen lo mismo
Usuario-1: Imprimir Trabajo A
Usuario-2: Imprimir Trabajo A       ← Trabajo duplicado!
```

#### Con Semáforo (✅ Correcto)
```
Usuario-1: acquire() → éxito
Usuario-1: Leer cola → [Trabajo A]
Usuario-2: acquire() → BLOQUEADO
Usuario-1: Imprimir Trabajo A
Usuario-1: release()
Usuario-2: acquire() → éxito (ahora puede continuar)
Usuario-2: Leer cola → [Trabajo B]  ← Lee el siguiente
```

### 3. Interbloqueo (Deadlock)

Este diseño **NO tiene riesgo de deadlock** porque:
- Solo hay un semáforo (no hay ciclo de espera)
- Los procesos siempre liberan el semáforo (usando `finally` en Python y lógica similar en Java)
- No hay espera circular

---

## 🧪 Casos de Prueba

### Prueba 1: Exclusión Mutua
```bash
# Ejecutar con muchos usuarios
python Main.py 10 5  # 10 usuarios, 5 trabajos cada uno
```
**Resultado esperado**: Ningún trabajo se imprime simultáneamente

### Prueba 2: Justicia (Fairness)
```bash
# Observar el orden de ejecución
python Main.py 5 3
```
**Resultado esperado**: Todos los usuarios eventualmente imprimen

### Prueba 3: Rendimiento
```bash
# Medir tiempo con muchos trabajos
time python Main.py 20 10
```
**Resultado esperado**: Tiempo proporcional al número de trabajos

---

## 🐛 Problemas Conocidos y Soluciones

### Java: Error `ClassNotFoundException`
**Causa**: Paquete incorrecto o clases no compiladas  
**Solución**: 
```bash
javac javaVersion/*.java
java javaVersion.GestorImpresion
```

### Python: `ModuleNotFoundError`
**Causa**: Archivo ejecutado desde directorio incorrecto  
**Solución**: 
```bash
cd Python
python Main.py
```

### Ambos: Procesos zombie
**Causa**: No se esperó correctamente a la finalización  
**Solución**: Siempre usar `process.join()` (Python) o `process.waitFor()` (Java)

---

## 📖 Referencias

- [Java ProcessBuilder Documentation](https://docs.oracle.com/javase/8/docs/api/java/lang/ProcessBuilder.html)
- [Python multiprocessing](https://docs.python.org/3/library/multiprocessing.html)
- [Semaphores - Operating Systems Concepts](https://www.os-book.com/)
- [Concurrent Programming in Java](https://gee.cs.oswego.edu/dl/cpj/)

---

## 👨‍💻 Autor

**Esteban Garcés Pérez**

- 🎓 Alumno de 2º DAM
- 📧 Contacto: [GitHub](https://github.com/Speeson)
- 📂 Repositorio: [ProgramacionServicios](https://github.com/Speeson/ProgramacionServicios)

---

## 📄 Licencia

Este proyecto es material académico para la asignatura de **Programación de Servicios y Procesos** (PSP) del ciclo de Desarrollo de Aplicaciones Multiplataforma (DAM).

---

## 🎓 Asignatura

**Programación de Servicios y Procesos**  
Ciclo Formativo de Grado Superior - Desarrollo de Aplicaciones Multiplataforma (DAM)  
Curso 2024/2025

---

## 🌟 Agradecimientos

- A los profesores de PSP por los conocimientos impartidos
- A la comunidad de desarrolladores por la documentación y recursos
- A todos los que contribuyen al aprendizaje de la programación concurrente

---

<div align="center">

**⭐ Si este proyecto te ha ayudado a entender la programación concurrente, considera darle una estrella ⭐**

</div>

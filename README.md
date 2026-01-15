# SDAS – Security Detection & Analysis System

<p align="center">
  <img src="https://img.shields.io/badge/Project-SDAS-black" />
  <img src="https://img.shields.io/badge/Language-Java%2017%2B-blue" />
  <img src="https://img.shields.io/badge/Status-Operational-success" />
  <img src="https://img.shields.io/badge/Purpose-Academic-orange" />
</p>

SDAS es un sistema didáctico diseñado para simular capacidades básicas de detección y análisis de amenazas. Su objetivo es ilustrar cómo un sistema de ciberseguridad puede:

✔ Monitorizar recursos del sistema  
✔ Analizar tráfico de red  
✔ Identificar patrones anómalos  
✔ Registrar evidencia forense  

El sistema se organiza en tres módulos:

- **Módulo 1 – Integridad de ficheros**
- **Módulo 2 – Análisis de tráfico de red**
- **Módulo 3 – Procesos y malware simulado**

---

## 📦 Arquitectura del proyecto

```text
src/
└── core/
    ├── CyberSecurityMonitor.java   // Main: arranca el SDAS
    ├── SDASService.java            // Orquesta los módulos
    └── SDASLogger.java             // Logger unificado (log_sdas.txt)

    integrity/
    ├── IntegrityMonitor.java       // Monitor de integridad (SHA-256)
    └── FileInfo.java               // Modelo: ruta + hash

    network/
    └── TrafficAnalyzer.java        // Tráfico simulado + reglas básicas

    process/
    ├── ProcessSimulator.java       // Genera processes.log
    └── ProcessAnalyzer.java        // Detecta CPU alta, lista negra, persistencia

```
El diseño favorece modularidad, lectura y extensión futura del sistema.
---

## 🚀 Ejecución del sistema

Ejecutable principal:

CyberSecurityMonitor.java


Este coordina los tres módulos de forma concurrente mediante `ScheduledExecutorService`.

---

## 🔍 Módulos

### ✔ Módulo 1 — Integridad

- Supervisa la carpeta `watch/`
- Detecta:
  - creación de ficheros
  - modificación de ficheros
  - eliminación de ficheros
- Registra eventos en el log general

Implementa hashing **SHA-256** para detectar alteraciones.

---

### ✔ Módulo 2 — Tráfico simulado

Genera tráfico en:

data/traffic.log


Simula puertos:

- **Normales:** 80, 443
- **Sospechosos / intrusión:** 22, 23, 135, 4444

---

### ✔ Módulo 3 — Procesos + malware

Genera procesos en:

data/processes.log


Incluye procesos de sistema y malware ficticio:

miner.exe
keylogger.exe
ransomware.exe
backup_agent.exe


Detecta comportamientos e imprime alertas.

---

## 🗂 Directorios necesarios

Antes de ejecutar, deben existir:

data/
watch/


>  crear manualmente y modificar y eliminar para ver su flujo.

---

## 📝 Logs generados

Durante la ejecución se generan:

log_sdas.txt (log principal)
data/processes.log
data/traffic.log


Estos sirven de evidencia para análisis posterior.

---

## ▶ Cómo ejecutar

### Desde IDE:

- Ejecutar `CyberSecurityMonitor`

### Desde terminal:

```sh
javac *.java
java CyberSecurityMonitor
```
(Dependiendo de la estructura real del proyecto)

## 🎯 Objetivo académico

Proyecto orientado al aprendizaje en:

monitorización de sistemas

ciberseguridad

modularidad

concurrencia

análisis de eventos y alertas

No es un IDS real, sino una maqueta simulada para docencia/demostración.

---

© 2026 — Iker Domínguez 


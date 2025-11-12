package javaVersion;

import java.io.*;
import java.util.*;

/**
 * Clase GestorImpresion - Proceso principal que coordina
 * el sistema de impresión multiproceso
 */
public class GestorImpresion {
    
    // Configuración del sistema
    private static final int NUM_USUARIOS = 3;
    private static final int TRABAJOS_POR_USUARIO = 2;
    private static final String SEMAFORO_FILE = "semaforo.lock";
    private static final String COLA_FILE = "cola_impresion.txt";
    
    public static void main(String[] args) {
        // Cabecera
        System.out.println("==================================================");
        System.out.println("SISTEMA DE IMPRESIÓN MULTIPROCESO");
        System.out.println("==================================================");
        
        // Limpiar archivos previos si existen
        limpiarArchivosControl();
        
        try {
            // Paso 1: Iniciar proceso de impresora (en background)
            Process procesoImpresora = iniciarProcesoImpresora();
            
            // Pequeña pausa para que la impresora se inicialice
            Thread.sleep(500);
            
            // Paso 2: Mostrar información
            System.out.println("\nIniciando " + NUM_USUARIOS + " usuarios...");
            System.out.println("Cada usuario enviará " + TRABAJOS_POR_USUARIO + " trabajos\n");
            
            // Paso 3: Crear y lanzar procesos de usuario
            List<Process> procesos = crearProcesosUsuario();
            
            // Paso 4: Esperar a que todos los usuarios terminen
            esperarFinalizacionProcesos(procesos);
            
            // Paso 5: Enviar señal de finalización a la impresora
            enviarSeñalFinalizacion();
            
            // Esperar a que la impresora termine
            procesoImpresora.waitFor();
            
            // Paso 6: Limpiar archivos de control
            limpiarArchivosControl();
            
            // Mensaje final
            System.out.println("\n==================================================");
            System.out.println("Todos los trabajos han sido impresos");
            System.out.println("==================================================");
            
        } catch (IOException e) {
            System.err.println("Error de E/S: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Proceso principal interrumpido");
            e.printStackTrace();
        }
    }
    
    /**
     * Limpia los archivos de control de ejecuciones previas
     */
    private static void limpiarArchivosControl() {
        File semaforo = new File(SEMAFORO_FILE);
        File cola = new File(COLA_FILE);
        
        if (semaforo.exists()) {
            semaforo.delete();
        }
        if (cola.exists()) {
            cola.delete();
        }
    }
    
    /**
     * Inicia el proceso de la impresora en segundo plano
     * @return El proceso de la impresora
     */
    private static Process iniciarProcesoImpresora() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
            "java", 
            "-cp",
            ".",
            "javaVersion.Impresora",
            COLA_FILE,
            SEMAFORO_FILE
        );
        pb.inheritIO();
        return pb.start(); // NO esperamos, se ejecuta en paralelo
    }
    
    /**
     * Crea y lanza todos los procesos de usuario
     * @return Lista de procesos lanzados
     */
    private static List<Process> crearProcesosUsuario() throws IOException {
        List<Process> procesos = new ArrayList<>();
        
        for (int i = 1; i <= NUM_USUARIOS; i++) {
            Process p = crearProcesoUsuario(i, TRABAJOS_POR_USUARIO);
            procesos.add(p);
            // Pequeña pausa entre lanzamientos para evitar condiciones de carrera
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        return procesos;
    }
    
    /**
     * Crea un proceso individual de usuario
     * @param id Identificador del usuario
     * @param trabajos Número de trabajos a realizar
     * @return Proceso creado y lanzado
     */
    private static Process crearProcesoUsuario(int id, int trabajos) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
            "java",
            "-cp",
            ".",
            "javaVersion.Usuario",
            String.valueOf(id),
            String.valueOf(trabajos),
            SEMAFORO_FILE,
            COLA_FILE
        );
        
        pb.inheritIO();
        return pb.start();
    }
    
    /**
     * Espera a que todos los procesos terminen
     * @param procesos Lista de procesos a esperar
     */
    private static void esperarFinalizacionProcesos(List<Process> procesos) throws InterruptedException {
        for (Process p : procesos) {
            p.waitFor();
        }
    }
    
    /**
     * Envía señal de finalización a la impresora escribiendo "FIN" en la cola
     */
    private static void enviarSeñalFinalizacion() throws IOException, InterruptedException {
        // Esperar a que no haya trabajos pendientes
        Thread.sleep(1000);
        
        // Escribir señal de finalización
        try (FileWriter fw = new FileWriter(COLA_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("FIN\n");
        }
    }
}
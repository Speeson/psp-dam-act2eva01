package javaVersion;

import java.io.*;
import java.util.*;

/**
 * Clase Impresora - Proceso que simula una impresora física
 * Lee trabajos de una cola y los procesa uno por uno
 */
public class Impresora {
    
    private static String colaFile;
    private static String semaforoFile;
    
    public static void main(String[] args) {
        // Validar argumentos
        if (args.length < 2) {
            System.err.println("Error: Faltan argumentos");
            System.err.println("Uso: java Impresora <archivoCola> <archivoSemaforo>");
            System.exit(1);
        }
        
        colaFile = args[0];
        semaforoFile = args[1];
        
        System.out.println("[Impresora] Impresora lista para recibir trabajos");
        
        // Bucle principal: procesar trabajos hasta recibir señal FIN
        while (true) {
            try {
                String trabajo = leerSiguienteTrabajo();
                
                if (trabajo == null) {
                    // No hay trabajos, esperar un poco
                    Thread.sleep(200);
                    continue;
                }
                
                if (trabajo.equals("FIN")) {
                    // Señal de finalización (sin mensaje)
                    break;
                }
                
                // Procesar el trabajo (sin mostrar nada, es silencioso)
                procesarTrabajo(trabajo);
                
            } catch (InterruptedException e) {
                break;
            } catch (IOException e) {
                System.err.println("[Impresora] Error de E/S: " + e.getMessage());
            }
        }
    }
    
    /**
     * Lee el siguiente trabajo de la cola
     * Usa el semáforo para acceso exclusivo al archivo
     * @return El contenido del trabajo o null si no hay trabajos
     */
    private static String leerSiguienteTrabajo() throws IOException, InterruptedException {
        // Adquirir semáforo para acceso exclusivo
        adquirirSemaforo();
        
        try {
            File cola = new File(colaFile);
            
            if (!cola.exists() || cola.length() == 0) {
                return null;
            }
            
            // Leer todos los trabajos
            List<String> trabajos = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(cola))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    trabajos.add(linea);
                }
            }
            
            if (trabajos.isEmpty()) {
                return null;
            }
            
            // Obtener el primer trabajo
            String primerTrabajo = trabajos.get(0);
            trabajos.remove(0);
            
            // Reescribir la cola sin el primer trabajo
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(cola))) {
                for (String trabajo : trabajos) {
                    bw.write(trabajo + "\n");
                }
            }
            
            return primerTrabajo;
            
        } finally {
            // Liberar semáforo
            liberarSemaforo();
        }
    }
    
    /**
     * Procesa (imprime) un trabajo
     * @param trabajo Descripción del trabajo a imprimir
     */
    private static void procesarTrabajo(String trabajo) throws InterruptedException {
        // La impresora procesa en silencio
        // (En realidad no necesita hacer nada, los usuarios ya simulan la impresión)
        Thread.sleep(100); // Pequeña pausa
    }
    
    /**
     * Adquiere el semáforo (espera activa hasta conseguirlo)
     */
    private static void adquirirSemaforo() throws IOException, InterruptedException {
        File semaforo = new File(semaforoFile);
        
        // Intentar crear el archivo (operación atómica)
        // Si ya existe, esperar y reintentar
        while (!semaforo.createNewFile()) {
            Thread.sleep(50); // Espera 50ms antes de reintentar
        }
    }
    
    /**
     * Libera el semáforo borrando el archivo
     */
    private static void liberarSemaforo() {
        File semaforo = new File(semaforoFile);
        semaforo.delete();
    }
}
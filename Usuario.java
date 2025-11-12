package javaVersion;

import java.io.*;

/**
 * Clase Usuario - Proceso independiente que simula un trabajador
 * enviando trabajos a la impresora compartida
 */
public class Usuario {
    
    public static void main(String[] args) {
        // Validar argumentos
        if (args.length < 4) {
            System.err.println("Error: Faltan argumentos");
            System.err.println("Uso: java Usuario <id> <numTrabajos> <archivoSemaforo> <archivoCola>");
            System.exit(1);
        }
        
        // Parsear argumentos
        int idUsuario = Integer.parseInt(args[0]);
        int numTrabajos = Integer.parseInt(args[1]);
        String archivoSemaforo = args[2];
        String archivoCola = args[3];
        
        // Cada usuario procesa sus trabajos
        for (int trabajo = 1; trabajo <= numTrabajos; trabajo++) {
            try {
                // Solicitar acceso a la impresora
                System.out.println("[Usuario-" + idUsuario + "] Solicita la impresora...");
                
                // ADQUIRIR SEMÁFORO (esperar hasta conseguir el turno)
                adquirirSemaforo(archivoSemaforo);
                
                // SECCIÓN CRÍTICA: Solo un proceso puede estar aquí
                System.out.println("[Usuario-" + idUsuario + "] Imprimiendo trabajo " + trabajo);
                
                // Enviar trabajo a la cola de impresión
                String descripcionTrabajo = "Usuario-" + idUsuario + " Trabajo-" + trabajo;
                enviarTrabajoACola(archivoCola, descripcionTrabajo);
                
                // Simular tiempo de impresión
                Thread.sleep(2000);
                
                System.out.println("[Usuario-" + idUsuario + "] Termina trabajo " + trabajo);
                
                // LIBERAR SEMÁFORO (devolver el turno)
                liberarSemaforo(archivoSemaforo);
                
                // Pausa entre trabajos del mismo usuario
                Thread.sleep(500);
                
            } catch (IOException e) {
                System.err.println("[Usuario-" + idUsuario + "] Error de E/S: " + e.getMessage());
            } catch (InterruptedException e) {
                System.err.println("[Usuario-" + idUsuario + "] Proceso interrumpido");
            }
        }
    }
    
    /**
     * Adquiere el semáforo (espera activa hasta conseguirlo)
     * @param archivo Nombre del archivo que actúa como semáforo
     */
    private static void adquirirSemaforo(String archivo) throws IOException, InterruptedException {
        File semaforo = new File(archivo);
        
        // Intentar crear el archivo (operación atómica)
        // Si ya existe, esperar y reintentar (polling)
        while (!semaforo.createNewFile()) {
            Thread.sleep(100); // Espera 100ms antes de reintentar
        }
        // Si llegamos aquí, hemos conseguido crear el archivo = tenemos el semáforo
    }
    
    /**
     * Libera el semáforo borrando el archivo
     * @param archivo Nombre del archivo que actúa como semáforo
     */
    private static void liberarSemaforo(String archivo) {
        File semaforo = new File(archivo);
        semaforo.delete();
    }
    
    /**
     * Envía un trabajo a la cola de impresión
     * @param archivoCola Archivo que contiene la cola de trabajos
     * @param trabajo Descripción del trabajo a enviar
     */
    private static void enviarTrabajoACola(String archivoCola, String trabajo) throws IOException {
        try (FileWriter fw = new FileWriter(archivoCola, true);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(trabajo + "\n");
        }
    }
}
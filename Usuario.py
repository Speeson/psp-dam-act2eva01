"""
Clase Usuario - Representa un usuario que envía trabajos a la impresora
"""
import multiprocessing
import time


class Usuario:
    """
    Representa un usuario del sistema de impresión
    """
    
    def __init__(self, id_usuario, num_trabajos):
        """
        Constructor del usuario
        
        Args:
            id_usuario: Identificador único del usuario
            num_trabajos: Número de trabajos que enviará este usuario
        """
        self.id_usuario = id_usuario
        self.num_trabajos = num_trabajos
    
    def enviar_trabajos(self, semaforo):
        """
        Envía los trabajos a la impresora de forma secuencial
        Cada trabajo requiere adquirir el semáforo antes de imprimir
        
        Args:
            semaforo: Semáforo que controla el acceso a la impresora
        """
        for num_trabajo in range(1, self.num_trabajos + 1):
            print(f"[Usuario-{self.id_usuario}] Solicita la impresora...")
            
            # Adquirir el semáforo (esperar si está ocupado)
            semaforo.acquire()
            
            try:
                # Sección crítica: imprimir trabajo
                print(f"[Usuario-{self.id_usuario}] Imprimiendo trabajo {num_trabajo}")
                time.sleep(2)  # Simula el tiempo de impresión
                print(f"[Usuario-{self.id_usuario}] Termina trabajo {num_trabajo}")
            finally:
                # Liberar el semáforo
                semaforo.release()
            
            # Pequeña pausa entre trabajos del mismo usuario
            time.sleep(0.5)
    
    def __str__(self):
        """
        Representación en string del usuario
        """
        return f"Usuario-{self.id_usuario} ({self.num_trabajos} trabajos)"


def proceso_usuario(id_usuario, num_trabajos, semaforo):
    """
    Función que se ejecuta como proceso independiente
    Crea una instancia de Usuario y ejecuta sus trabajos
    
    Args:
        id_usuario: ID del usuario
        num_trabajos: Número de trabajos a enviar
        semaforo: Semáforo compartido para sincronización
    """
    usuario = Usuario(id_usuario, num_trabajos)
    usuario.enviar_trabajos(semaforo)
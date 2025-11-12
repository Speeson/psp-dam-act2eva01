"""
Clase Impresora - Representa el recurso compartido (impresora)
"""
import multiprocessing


class Impresora:
    """
    Representa la impresora como recurso compartido
    En este diseño, la impresora no necesita lógica activa,
    ya que el semáforo controla el acceso
    """
    
    def __init__(self):
        """
        Constructor de la impresora
        """
        self.nombre = "Impresora Principal"
        self.estado = "Disponible"
    
    def inicializar(self):
        """
        Inicializa la impresora y muestra que está lista
        """
        print(f"[{self.nombre}] Impresora lista para recibir trabajos")
        self.estado = "Activa"
    
    def obtener_estado(self):
        """
        Retorna el estado actual de la impresora
        
        Returns:
            Estado de la impresora
        """
        return self.estado
    
    def __str__(self):
        """
        Representación en string de la impresora
        """
        return f"{self.nombre} - Estado: {self.estado}"


def proceso_impresora():
    """
    Función que se ejecuta como proceso de la impresora
    Crea una instancia de Impresora y la inicializa
    """
    impresora = Impresora()
    impresora.inicializar()
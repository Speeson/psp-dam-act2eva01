"""
Clase GestorImpresion - Coordina el sistema de impresión multiproceso
"""
import multiprocessing
from Usuario import proceso_usuario
from Impresora import proceso_impresora


class GestorImpresion:
    """
    Proceso principal que coordina todos los procesos
    Gestiona el semáforo binario para exclusión mutua
    """
    
    def __init__(self, num_usuarios=3, trabajos_por_usuario=2):
        """
        Constructor del gestor de impresión
        
        Args:
            num_usuarios: Número de usuarios concurrentes (default: 3)
            trabajos_por_usuario: Trabajos que enviará cada usuario (default: 2)
        """
        self.num_usuarios = num_usuarios
        self.trabajos_por_usuario = trabajos_por_usuario
        # Semáforo binario (valor inicial = 1)
        # Solo un proceso puede tener el recurso a la vez
        self.semaforo = multiprocessing.Semaphore(1)
        self.procesos = []
    
    def mostrar_cabecera(self):
        """
        Muestra la cabecera del sistema
        """
        print("=" * 50)
        print("SISTEMA DE IMPRESIÓN MULTIPROCESO")
        print("=" * 50)
    
    def inicializar_impresora(self):
        """
        Crea e inicializa el proceso de la impresora
        """
        proceso_imp = multiprocessing.Process(target=proceso_impresora)
        proceso_imp.start()
        proceso_imp.join()
    
    def crear_procesos_usuarios(self):
        """
        Crea y lanza todos los procesos de usuario
        """
        print(f"\nIniciando {self.num_usuarios} usuarios...")
        print(f"Cada usuario enviará {self.trabajos_por_usuario} trabajos\n")
        
        for i in range(1, self.num_usuarios + 1):
            p = multiprocessing.Process(
                target=proceso_usuario,
                args=(i, self.trabajos_por_usuario, self.semaforo)
            )
            self.procesos.append(p)
            p.start()
    
    def esperar_finalizacion(self):
        """
        Espera a que todos los procesos terminen
        """
        for p in self.procesos:
            p.join()
    
    def mostrar_pie(self):
        """
        Muestra el mensaje final
        """
        print("\n" + "=" * 50)
        print("Todos los trabajos han sido impresos")
        print("=" * 50)
    
    def iniciar_sistema(self):
        """
        Método principal que coordina todo el flujo del sistema
        """
        self.mostrar_cabecera()
        self.inicializar_impresora()
        self.crear_procesos_usuarios()
        self.esperar_finalizacion()
        self.mostrar_pie()
    
    def obtener_info_sistema(self):
        """
        Retorna información sobre la configuración del sistema
        
        Returns:
            Diccionario con información del sistema
        """
        return {
            'usuarios': self.num_usuarios,
            'trabajos_por_usuario': self.trabajos_por_usuario,
            'total_trabajos': self.num_usuarios * self.trabajos_por_usuario,
            'procesos_activos': len([p for p in self.procesos if p.is_alive()])
        }
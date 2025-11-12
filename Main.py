"""
PSP-DAM-ACTEVA02
Sistema de impresión multiproceso con semáforos
Autor: Sistema de gestión de impresión compartida

Clase Main - Punto de entrada principal del programa
"""
import sys
from GestorImpresion import GestorImpresion


class Main:
    """
    Clase principal que contiene el punto de entrada del programa
    Similar al patrón de Java con método main estático
    """
    
    @staticmethod
    def main():
        """
        Punto de entrada principal del programa
        Crea y ejecuta el gestor de impresión
        """
        # Crear gestor con 3 usuarios, 2 trabajos cada uno
        gestor = GestorImpresion(num_usuarios=3, trabajos_por_usuario=2)
        gestor.iniciar_sistema()
    
    @staticmethod
    def main_con_parametros(num_usuarios, trabajos_por_usuario):
        """
        Versión alternativa del main que acepta parámetros
        
        Args:
            num_usuarios: Número de usuarios concurrentes
            trabajos_por_usuario: Trabajos que enviará cada usuario
        """
        gestor = GestorImpresion(num_usuarios, trabajos_por_usuario)
        gestor.iniciar_sistema()


# Protección necesaria para multiprocessing
if __name__ == '__main__':
    # Verificar si se pasaron argumentos por línea de comandos
    if len(sys.argv) == 3:
        try:
            usuarios = int(sys.argv[1])
            trabajos = int(sys.argv[2])
            Main.main_con_parametros(usuarios, trabajos)
        except ValueError:
            print("Error: Los parámetros deben ser números enteros")
            print("Uso: python Main.py <num_usuarios> <trabajos_por_usuario>")
            sys.exit(1)
    else:
        # Usar valores por defecto
        Main.main()
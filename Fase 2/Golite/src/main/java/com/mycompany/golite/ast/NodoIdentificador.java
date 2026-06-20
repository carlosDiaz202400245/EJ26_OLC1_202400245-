package com.mycompany.golite.ast;

/**
 * Nodo que representa el acceso a una variable por su nombre.
 * Ejemplos: x, miVar, contador
 *
 * Cuando se ejecuta, busca el nombre en el entorno actual
 * y devuelve su valor Si no existe lanza semantico
 */
public class NodoIdentificador extends Nodo {

    public String nombre;

    /**
     * @param nombre  nombre de la variable
     * @param linea   línea en el código fuente
     * @param columna columna en el código fuente
     */
    public NodoIdentificador(String nombre, int linea, int columna) {
        super(linea, columna);
        this.nombre = nombre;
    }

    /**
     * Busca la variable en el entorno y devuelve su valor.
     * Si la variable no existe en ningún scope lanza un error semántico.
     */
    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        // Verificar que la variable existe
        if (!entorno.existe(nombre)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": La variable '" + nombre + "' no está definida en este ámbito."
            );
        }
        return entorno.obtener(nombre);
    }

    @Override
    public String toString() {
        return "ID(" + nombre + ")";
    }
}
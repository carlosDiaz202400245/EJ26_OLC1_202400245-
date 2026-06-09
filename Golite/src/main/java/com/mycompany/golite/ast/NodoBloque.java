package com.mycompany.golite.ast;

import java.util.List;

/**
 * Nodo que representa un bloque de sentencias delimitado por llaves { }.
 *
 * Cada bloque crea su propio alcance o scoup local.
 * Las variables declaradas dentro solo son visibles dentro del bloque
 * y en bloques anidados.
 
 */
public class NodoBloque extends Nodo {

    /** Lista de sentencias dentro del bloque */
    public List<Nodo> sentencias;

    /**
     * @param sentencias lista de nodos que conforman el bloque
     * @param linea      línea en el código fuente
     * @param columna    columna en el código fuente
     */
    public NodoBloque(List<Nodo> sentencias, int linea, int columna) {
        super(linea, columna);
        this.sentencias = sentencias;
    }

    /**
     * Ejecuta todas las sentencias del bloque en orden.
     * Crea un nuevo scope hijo del entorno actual y lo destruye al terminar.
     * Si alguna sentencia lanza una señal de control 
     * la propaga hacia arriba para que la maneje el for o la función.
     */
    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        // Crear un nuevo scope local para este bloque
        com.mycompany.golite.Entorno scopeLocal = new com.mycompany.golite.Entorno(entorno);

        for (Nodo sentencia : sentencias) {
            Object resultado = sentencia.ejecutar(scopeLocal);

            // Si la sentencia retornó una señal de control, propagarla
            // Las señales son BreakSignal, ContinueSignal, ReturnSignal
            if (resultado instanceof com.mycompany.golite.Entorno.Senal) {
                return resultado;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Bloque{\n");
        for (Nodo s : sentencias) {
            sb.append("  ").append(s).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
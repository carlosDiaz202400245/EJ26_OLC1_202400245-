package com.mycompany.golite.ast;

import java.util.List;

/**
 * Nodo de un bloque de sentencias entre llaves { }. Cada bloque crea su propio
 * scope local; las variables declaradas dentro solo son visibles ahí y en bloques anidados.
 */
public class NodoBloque extends Nodo {

    public List<Nodo> sentencias;

    public NodoBloque(List<Nodo> sentencias, int linea, int columna) {
        super(linea, columna);
        this.sentencias = sentencias;
    }

    /**
     * Ejecuta las sentencias en orden dentro de un scope hijo. Si alguna devuelve
     * una señal de control (break/continue/return) la propaga hacia arriba.
     */
    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {

        com.mycompany.golite.Entorno scopeLocal = new com.mycompany.golite.Entorno(entorno);

        for (Nodo sentencia : sentencias) {
            Object resultado = sentencia.ejecutar(scopeLocal);

            // Propagar señales de control (BreakSignal, ContinueSignal, ReturnSignal)
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
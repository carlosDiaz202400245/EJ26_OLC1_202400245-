package com.mycompany.golite.ast;

/**
 * Nodo que representa la sentencia break.
 * Solo válido dentro de un bucle for.
 */
public class NodoBreak extends Nodo {

    public NodoBreak(int linea, int columna) {
        super(linea, columna);
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        return new com.mycompany.golite.Entorno.BreakSignal();
    }

    @Override
    public String toString() {
        return "Break";
    }
}
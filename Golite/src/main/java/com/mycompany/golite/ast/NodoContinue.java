package com.mycompany.golite.ast;

/**
 * Nodo que representa la sentencia continue.
 * Solo válido dentro de un bucle for xd
 */
public class NodoContinue extends Nodo {

    public NodoContinue(int linea, int columna) {
        super(linea, columna);
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        return new com.mycompany.golite.Entorno.ContinueSignal();
    }

    @Override
    public String toString() {
        return "Continue";
    }
}
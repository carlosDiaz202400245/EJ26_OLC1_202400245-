package com.mycompany.golite.ast;

import java.util.List;

/** Función len: longitud de un slice o de un string. */
public class NodoLen extends Nodo {

    public Nodo expresion;

    public NodoLen(Nodo expresion, int linea, int columna) {
        super(linea, columna);
        this.expresion = expresion;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object v = expresion.ejecutar(entorno);
        if (v instanceof List)   return ((List<?>) v).size();
        if (v instanceof String) return ((String) v).length();
        throw new RuntimeException(
            "[Error Semántico] Línea " + linea + ", Columna " + columna
            + ": len solo aplica a slice o string.");
    }

    @Override
    public String toString() {
        return "len(" + expresion + ")";
    }
}

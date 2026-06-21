package com.mycompany.golite.ast;

import java.util.List;

/** Asignación a un elemento de slice: s[i] = valor. */
public class NodoAsignacionIndice extends Nodo {

    public Nodo base;
    public Nodo indice;
    public Nodo valor;

    public NodoAsignacionIndice(Nodo base, Nodo indice, Nodo valor, int linea, int columna) {
        super(linea, columna);
        this.base   = base;
        this.indice = indice;
        this.valor  = valor;
    }

    @Override
    public Object ejecutar(com.mycompany.golite.Entorno entorno) {
        Object col = base.ejecutar(entorno);
        Object idxObj = indice.ejecutar(entorno);

        if (!(col instanceof List)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": solo se puede indexar y asignar sobre un slice.");
        }
        if (!(idxObj instanceof Integer)) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": el índice debe ser int.");
        }

        @SuppressWarnings("unchecked")
        List<Object> lista = (List<Object>) col;
        int idx = (Integer) idxObj;
        if (idx < 0 || idx >= lista.size()) {
            throw new RuntimeException(
                "[Error Semántico] Línea " + linea + ", Columna " + columna
                + ": índice " + idx + " fuera de rango (tamaño " + lista.size() + ").");
        }

        lista.set(idx, valor.ejecutar(entorno));
        return null;
    }

    @Override
    public String toString() {
        return base + "[" + indice + "] = " + valor;
    }
}
